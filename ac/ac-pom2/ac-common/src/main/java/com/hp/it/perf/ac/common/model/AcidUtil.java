package com.hp.it.perf.ac.common.model;

@Deprecated
class AcidUtil {

	private static final int CATEGORY_BITS = 8;
	private static final int TYPE_BITS = 5;
	private static final int LEVEL_BITS = 3;
	private static final int RESERVED_BITS = 4;
	private static final int PROFILE_SID_BITS = 64 - CATEGORY_BITS - TYPE_BITS
			- LEVEL_BITS - RESERVED_BITS;

	private static final int R_SID_BITS = 29;
	private static final int B_SID_BITS = 38;

	private static final int RB_SIGN_BIT = 1;
	private static final int R_PROFILE_BITS = PROFILE_SID_BITS - RB_SIGN_BIT
			- R_SID_BITS;
	private static final int B_PROFILE_BITS = PROFILE_SID_BITS - RB_SIGN_BIT
			- B_SID_BITS;
	private static final long RB_SIGN_MASK = 1L << (64 - RB_SIGN_BIT);

	public static long getAcid(int profile, long sid, int category, int type,
			int level) {
		validate(category, CATEGORY_BITS, "Category");
		validate(type, TYPE_BITS, "Type");
		validate(level, LEVEL_BITS, "Level");

		long acid = 0L;
		if (isRegularProfile(profile)) {
			validateProfile(profile, true, "R_Profile");
			validate(sid, R_SID_BITS, "R_Sid");
			acid |= (((long) profile) << (64 - R_PROFILE_BITS - RB_SIGN_BIT));
			acid |= ((long) sid) << (64 - R_SID_BITS) >>> (RB_SIGN_BIT + R_PROFILE_BITS);
		} else {
			validateProfile(profile, false, "B_Profile");
			validate(sid, B_SID_BITS, "B_Sid");
			acid |= ((((long) profile) << (64 - B_PROFILE_BITS)) >>> RB_SIGN_BIT)
					| RB_SIGN_MASK;
			acid |= ((long) sid) << (64 - B_SID_BITS) >>> (RB_SIGN_BIT + B_PROFILE_BITS);
		}

		acid |= ((long) category) << (64 - CATEGORY_BITS) >>> PROFILE_SID_BITS;
		acid |= ((long) type) << (64 - TYPE_BITS) >>> (PROFILE_SID_BITS + CATEGORY_BITS);
		acid |= ((long) level) << (64 - LEVEL_BITS) >>> (PROFILE_SID_BITS
				+ CATEGORY_BITS + TYPE_BITS);

		return acid;

	}

	private static void validateProfile(int profile, boolean isRegular,
			String name) throws IllegalArgumentException {
		int max = getProfileBySeed((2 << (isRegular ? R_PROFILE_BITS
				: B_PROFILE_BITS)) - 1, isRegular);
		int min = getProfileBySeed(0, isRegular);
		if (profile >= min && profile <= max)
			return;
		throw new IllegalArgumentException(name + " is OutOfRange. Value is: "
				+ profile + ". minValue is: " + min + ". maxValue is: " + max);

	}

	public static int getProfileBySeed(int seed, boolean isRegular) {
		if (isRegular) {
			return (int) (((long) seed) << (64 - R_PROFILE_BITS) >>> (64 - R_PROFILE_BITS));
		} else {
			// return (int) (((long) seed) & 0x3FF | 0x4000);
			return (int) ((((long) seed << (64 - B_PROFILE_BITS) >>> (R_PROFILE_BITS
					- B_PROFILE_BITS + RB_SIGN_BIT)) | RB_SIGN_MASK) >>> (64 - R_PROFILE_BITS - RB_SIGN_BIT));
		}
	}

	private static void validate(long value, int bits, String name)
			throws IllegalArgumentException {
		if (value >= 0 && value < (1L << bits))
			return;
		throw new IllegalArgumentException(name + " is OutOfRange. Value is: "
				+ value + " maxValue is: " + (1L << bits));
	}

	public static int getProfile(long acid) {
		if (acid > 0) {
			// regular acid
			long result = acid >>> (64 - R_PROFILE_BITS - RB_SIGN_BIT);
			return (int) result;
		} else {
			// big acid
			// long result = (acid >> (R_PROFILE_BITS - B_PROFILE_BITS) >>> (64
			// - R_PROFILE_BITS - RB_SIGN_BIT)) & 0x43FF;
			long result = (((acid ^ RB_SIGN_MASK) >>> (R_PROFILE_BITS
					- B_PROFILE_BITS + RB_SIGN_BIT)) | RB_SIGN_MASK) >>> (64 - R_PROFILE_BITS - RB_SIGN_BIT);
			return (int) result;
		}
	}

	public static boolean isRegular(long acid) {
		return acid > 0;
	}

	public static boolean isRegularProfile(int profile) {
		return ((long) profile) >> R_PROFILE_BITS << (64 - RB_SIGN_BIT) == 0;
	}

	public static long getSid(long acid) {
		if (acid > 0) {
			long result = acid << (R_PROFILE_BITS + RB_SIGN_BIT) >>> (64 - R_SID_BITS);
			return result;
		}
		long result = acid << (B_PROFILE_BITS + RB_SIGN_BIT) >>> (64 - B_SID_BITS);
		return result;
	}

	public static int getCategory(long acid) {
		return (int) (acid << PROFILE_SID_BITS >>> (64 - CATEGORY_BITS));
	}

	public static int getType(long acid) {
		return (int) (acid << (PROFILE_SID_BITS + CATEGORY_BITS) >>> (64 - TYPE_BITS));
	}

	public static int getLevel(long acid) {
		return (int) (acid << (PROFILE_SID_BITS + CATEGORY_BITS + TYPE_BITS) >>> (64 - LEVEL_BITS));
	}

	public static String toHexString(long acid) {
		return Long.toHexString(acid).toUpperCase();
	}

	public static String toMeaningfulString(long acid, AcDictionary dictionary) {
		boolean isRegular = isRegular(acid);
		int profile = getProfile(acid);
		int category = getCategory(acid);
		int type = getType(acid);
		int level = getLevel(acid);
		long sid = getSid(acid);
		AcCategory acCategory = dictionary.category(category);
		AcType acType = acCategory.type(type);
		AcLevel acLevel = acCategory.level(level);

		String result = (isRegular ? "R" : "L") + "_" + String.valueOf(profile)
				+ "_" + String.valueOf(sid) + "_" + acCategory.name() + "_"
				+ acType.name() + "_" + acLevel.name();
		return result;
	}
	
	public static String toString(long acid) {
		if (isUnassigned(acid)) {
			return toHexString(acid) + "(" + acid + "->unassigned)";
		}
		boolean isRegular = isRegular(acid);
		int profile = getProfile(acid);
		int category = getCategory(acid);
		int type = getType(acid);
		int level = getLevel(acid);
		long sid = getSid(acid);
		String result = toHexString(acid) + "(" + acid + "->"
				+ (isRegular ? "" : "B") + String.valueOf(profile)
				+ "_S" + String.valueOf(sid) + "_C" + category + "_T" + type
				+ "_L" + level + ")";
		return result;
	}

	public static boolean isUnassigned(long acid) {
		return acid == 0;
	}

	public static long setCategory(long acid, int category) {
		validate(category, CATEGORY_BITS, "Category");
		long value = acid << PROFILE_SID_BITS >>> (64 - CATEGORY_BITS);
		value ^= (long) category; // XOR previous value
		acid ^= value << (64 - CATEGORY_BITS) >>> PROFILE_SID_BITS;
		return acid;
	}

	public static long setType(long acid, int type) {
		validate(type, TYPE_BITS, "Type");
		long value = acid << (PROFILE_SID_BITS + CATEGORY_BITS) >>> (64 - TYPE_BITS);
		value ^= (long) type; // XOR previous value
		acid ^= value << (64 - TYPE_BITS) >>> (PROFILE_SID_BITS + CATEGORY_BITS);
		return acid;
	}

	public static long setLevel(long acid, int level) {
		validate(level, LEVEL_BITS, "Level");
		long value = acid << (PROFILE_SID_BITS + CATEGORY_BITS + TYPE_BITS) >>> (64 - LEVEL_BITS);
		value ^= (long) level; // XOR previous value
		acid ^= value << (64 - LEVEL_BITS) >>> (PROFILE_SID_BITS
				+ CATEGORY_BITS + TYPE_BITS);
		return acid;
	}

}

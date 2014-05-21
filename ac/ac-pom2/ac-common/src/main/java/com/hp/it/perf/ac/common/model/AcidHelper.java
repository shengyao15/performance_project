package com.hp.it.perf.ac.common.model;

public class AcidHelper {

	private static interface AcidMask {

		public long decode(long value);

		public long encode(long acid, long value);

		public long mask(long acid, boolean mask);

		public int getRightBit();

	}

	private static class SingleBitsMask implements AcidMask {

		private final String name;

		private final int leftBit;
		private final int rightBit;
		private final int unmaskBit;
		private final long minValue;
		private final long maxValue;
		private final long maskValue;
		private final long unmaskValue;

		SingleBitsMask(String name, int leftBit, int maskBit) {
			if (name == null) {
				throw new IllegalArgumentException("null mask name");
			}
			this.name = name;
			if (leftBit < 0 || leftBit > 64) {
				throw new IndexOutOfBoundsException(
						"left bit is out of range [0, 64]: " + leftBit);
			}
			this.leftBit = leftBit;
			if (maskBit < 0 || maskBit > 64) {
				throw new IndexOutOfBoundsException(
						"mask bit is out of range [0, 64]: " + maskBit);
			}
			this.rightBit = 64 - maskBit - leftBit;
			if (rightBit < 0 || rightBit > 64) {
				throw new IndexOutOfBoundsException(
						"right bit is out of range [0, 64]: " + maskBit);
			}
			this.unmaskBit = 64 - maskBit;
			this.minValue = 0;
			this.maxValue = (1L << maskBit) - 1;
			// for or operation to set, like 000_1111_000
			this.maskValue = (~0L) << unmaskBit >>> leftBit;
			// for and operation to clear, like 111_0000_111
			this.unmaskValue = ~maskValue;
		}

		public int getRightBit() {
			return rightBit;
		}

		public long decode(long value) {
			return (value << leftBit >>> unmaskBit);
		}

		public long encode(long acid, long value) {
			if (value >= minValue && value <= maxValue) {
				// clear mask bits
				acid &= unmaskValue;
				// set mask bits
				acid |= (value << rightBit) & maskValue;
				return acid;
			}
			throw new IllegalArgumentException(name
					+ " is OutOfRange. Value is: " + value
					+ ", accept range is is: [" + minValue + ", " + maxValue
					+ "]");
		}

		@Override
		public long mask(long acid, boolean mask) {
			if (mask) {
				return acid | maskValue;
			} else {
				return acid & unmaskValue;
			}
		}

	}

	private static AcidHelper instance = new AcidHelper();

	// version 1 for Acid
	// reserve1: (3) 0 - 2
	private final AcidMask reserve1Mask = new SingleBitsMask("Reserve1", 0, 3);
	// profile: (6) 3 - 8
	private final AcidMask profileMask = new SingleBitsMask("Profile",
			64 - reserve1Mask.getRightBit(), 6);
	// reserve2: (1) 9 - 9
	private final AcidMask reserve2Mask = new SingleBitsMask("Reserve2",
			64 - profileMask.getRightBit(), 1);
	// category: (8) 10 - 17
	private final AcidMask categoryMask = new SingleBitsMask("Category",
			64 - reserve2Mask.getRightBit(), 8);
	// type: (5) 18 - 22
	private final AcidMask typeMask = new SingleBitsMask("Type",
			64 - categoryMask.getRightBit(), 5);
	// level: (3) 23 - 25
	private final AcidMask levelMask = new SingleBitsMask("Level",
			64 - typeMask.getRightBit(), 3);
	// reseve3: (1) 26 - 26
	private final AcidMask reserve3Mask = new SingleBitsMask("Reserve2",
			64 - levelMask.getRightBit(), 1);
	// sid: (37) 27 - 63
	private final AcidMask sidMask = new SingleBitsMask("Sid",
			64 - reserve3Mask.getRightBit(), reserve3Mask.getRightBit());

	private final int RESEVED_VALUE = 0;

	private AcidHelper() {
	}

	public static AcidHelper getInstance() {
		return instance;
	}

	public long getAcid(int profile, long sid, int category, int type, int level) {
		long acid = 0L;

		acid = reserve1Mask.encode(acid, RESEVED_VALUE);
		acid = reserve2Mask.encode(acid, RESEVED_VALUE);
		acid = reserve3Mask.encode(acid, RESEVED_VALUE);
		acid = profileMask.encode(acid, profile);
		acid = categoryMask.encode(acid, category);
		acid = typeMask.encode(acid, type);
		acid = levelMask.encode(acid, level);
		acid = sidMask.encode(acid, sid);

		return acid;
	}

	public int getProfile(long acid) {
		return (int) profileMask.decode(acid);
	}

	public long getSid(long acid) {
		return sidMask.decode(acid);
	}

	public int getCategory(long acid) {
		return (int) categoryMask.decode(acid);
	}

	public int getType(long acid) {
		return (int) typeMask.decode(acid);
	}

	public int getLevel(long acid) {
		return (int) levelMask.decode(acid);
	}

	public static boolean isUnassigned(long acid) {
		return acid == 0;
	}

	public String toMeaningfulString(long acid, AcDictionary dictionary) {
		int profile = getProfile(acid);
		int category = getCategory(acid);
		int type = getType(acid);
		int level = getLevel(acid);
		long sid = getSid(acid);
		AcCategory acCategory = dictionary.category(category);
		AcType acType = acCategory.type(type);
		AcLevel acLevel = acCategory.level(level);

		String result = String.valueOf(profile) + "_" + String.valueOf(sid)
				+ "_" + acCategory.name() + "_" + acType.name() + "_"
				+ acLevel.name();
		return result;
	}

	public String toHexString(long acid) {
		return Long.toHexString(acid).toUpperCase();
	}

	public long parseHexString(String str) {
		return Long.parseLong(str, 16);
	}
	
	public String toString(long acid) {
		if (isUnassigned(acid)) {
			return toHexString(acid) + "(" + acid + "->unassigned)";
		}
		int profile = getProfile(acid);
		int category = getCategory(acid);
		int type = getType(acid);
		int level = getLevel(acid);
		long sid = getSid(acid);
		String result = toHexString(acid) + "(" + acid + "->"
				+ String.valueOf(profile) + "_S" + String.valueOf(sid) + "_C"
				+ category + "_T" + type + "_L" + level + ")";
		return result;
	}

	public long setCategory(long acid, int category) {
		return categoryMask.encode(acid, category);
	}

	public long setType(long acid, int type) {
		return typeMask.encode(acid, type);
	}

	public long setLevel(long acid, int level) {
		return levelMask.encode(acid, level);
	}

	// package-private
	long getMaskValue(int profile, int category, int type, int level, long sid,
			boolean mask) {
		long acid = 0L;

		acid = reserve1Mask.encode(acid, RESEVED_VALUE);
		acid = reserve2Mask.encode(acid, RESEVED_VALUE);
		acid = reserve3Mask.encode(acid, RESEVED_VALUE);
		if (profile >= 0) {
			acid = profileMask.encode(acid, profile);
		} else {
			acid = profileMask.mask(acid, mask);
		}
		if (category >= 0) {
			acid = categoryMask.encode(acid, category);
		} else {
			acid = categoryMask.mask(acid, mask);
		}
		if (type >= 0) {
			acid = typeMask.encode(acid, type);
		} else {
			acid = typeMask.mask(acid, mask);
		}
		if (level >= 0) {
			acid = levelMask.encode(acid, level);
		} else {
			acid = levelMask.mask(acid, mask);
		}
		if (sid >= 0) {
			acid = sidMask.encode(acid, sid);
		} else {
			acid = sidMask.mask(acid, mask);
		}

		return acid;
	}

}

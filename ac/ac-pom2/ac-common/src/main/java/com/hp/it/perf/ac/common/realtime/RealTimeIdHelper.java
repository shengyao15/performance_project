package com.hp.it.perf.ac.common.realtime;


public class RealTimeIdHelper {
	
	private static interface IdMask {

		public int decode(int value);

		public int encode(int id, int value);

		public int mask(int id, boolean mask);

		public int getRightBit();

	}

	private static class SingleBitsMask implements IdMask {

		private final String name;

		private final int leftBit;
		private final int rightBit;
		private final int unmaskBit;
		private final int minValue;
		private final int maxValue;
		private final int maskValue;
		private final int unmaskValue;

		SingleBitsMask(String name, int leftBit, int maskBit) {
			if (name == null) {
				throw new IllegalArgumentException("null mask name");
			}
			this.name = name;
			if (leftBit < 0 || leftBit > 32) {
				throw new IndexOutOfBoundsException(
						"left bit is out of range [0, 64]: " + leftBit);
			}
			this.leftBit = leftBit;
			if (maskBit < 0 || maskBit > 32) {
				throw new IndexOutOfBoundsException(
						"mask bit is out of range [0, 64]: " + maskBit);
			}
			this.rightBit = 32 - maskBit - leftBit;
			if (rightBit < 0 || rightBit > 32) {
				throw new IndexOutOfBoundsException(
						"right bit is out of range [0, 64]: " + maskBit);
			}
			this.unmaskBit = 32 - maskBit;
			this.minValue = 0;
			this.maxValue = (1 << maskBit) - 1;
			// for or operation to set, like 000_1111_000
			this.maskValue = (~0) << unmaskBit >>> leftBit;
			// for and operation to clear, like 111_0000_111
			this.unmaskValue = ~maskValue;
		}

		public int getRightBit() {
			return rightBit;
		}

		public int decode(int value) {
			return (value << leftBit >>> unmaskBit);
		}

		public int encode(int acid, int value) {
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
		public int mask(int acid, boolean mask) {
			if (mask) {
				return acid | maskValue;
			} else {
				return acid & unmaskValue;
			}
		}

	}
	
	private static RealTimeIdHelper instance = new RealTimeIdHelper();
	
	// reserve1: (3) 0 - 7
	private final IdMask valueTypeMask = new SingleBitsMask("valueType", 0, 8);
	private final IdMask granularityTypeMask = new SingleBitsMask("granularityType", 32 - valueTypeMask.getRightBit(), 8);
	private final IdMask categoryMask = new SingleBitsMask("category", 32 - granularityTypeMask.getRightBit(), 8);
	private final IdMask featureTypeMask = new SingleBitsMask("featureType", 32 - categoryMask.getRightBit(), 8);

	public static RealTimeIdHelper getInstance() {
		return instance;
	}
	
	public int getId(int valueType, int granulityType, int category, int featureType) {
		/*int id = (valueType << 24) & 0xff000000 + (granulityType << 16)
				& 0x00ff0000 + (category << 8) & 0x0000ff00
				+ featureType & 0x000000ff;*/
		int id = 0;
		id |= valueTypeMask.encode(id, valueType);
		id |=granularityTypeMask.encode(id, granulityType);
		id |=categoryMask.encode(id, category);
		id |=featureTypeMask.encode(id, featureType);
		return id;
	}
	
	public int getValueType(int id) {
		return valueTypeMask.decode(id);
	}
	
	public int getGranulityType(int id) {
		return granularityTypeMask.decode(id);
	}
	
	public int getCategory(int id) {
		return categoryMask.decode(id);
	}
	
	public int getFeatureType(int id) {
		return featureTypeMask.decode(id);
	}
}

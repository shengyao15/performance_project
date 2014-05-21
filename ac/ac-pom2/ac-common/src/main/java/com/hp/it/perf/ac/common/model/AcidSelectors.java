package com.hp.it.perf.ac.common.model;

import java.io.Serializable;
import java.util.BitSet;

public final class AcidSelectors {

	public static abstract class AcidSelector implements Serializable {

		private static final long serialVersionUID = 1803243906621534638L;

		public abstract boolean accept(long acid);

		public long[] filter(long[] acidList) {
			BitSet filtered = new BitSet();
			int index = 0;
			for (long acid : acidList) {
				if (accept(acid)) {
					filtered.set(index++);
				}
			}
			if (index == acidList.length) {
				// return it directly as all included
				return acidList;
			} else {
				long[] retList = new long[index];
				index = 0;
				for (int i = filtered.nextSetBit(0); i >= 0; i = filtered
						.nextSetBit(i + 1)) {
					retList[index++] = acidList[i];
				}
				return retList;
			}
		}

		public abstract long getMaxBound();

		public abstract long getMinBound();

		public abstract Serializable getSelectorObject();
	}

	public final static class AcidRange extends AcidSelector {

		private static final long serialVersionUID = -4295423124753306569L;

		private final long rangeFrom;
		private final long rangeTo;
		private final Serializable selectorObject;

		public AcidRange(long rangeFrom, long rangeTo,
				Serializable selectorObject) {
			this.rangeFrom = rangeFrom;
			this.rangeTo = rangeTo;
			this.selectorObject = selectorObject;
		}

		public long getFrom() {
			return rangeFrom;
		}

		public long getTo() {
			return rangeTo;
		}

		@Override
		public Serializable getSelectorObject() {
			return selectorObject;
		}

		@Override
		public boolean accept(long acid) {
			return acid <= rangeTo && acid >= rangeFrom;
		}

		@Override
		public long getMaxBound() {
			return rangeTo;
		}

		@Override
		public long getMinBound() {
			return rangeFrom;
		}

		public String toString() {
			return "[" + getMinBound() + ", " + getMaxBound() + "]";
		}

	}

	public final static class SingleAcidSelector implements Serializable {

		private static final long serialVersionUID = 535948773022208579L;

		private int profile = -1;
		private long sid = -1;
		private int category = -1;
		private int type = -1;
		private int level = -1;

		public SingleAcidSelector profile(int profile) {
			this.profile = profile;
			return this;
		}

		public SingleAcidSelector sid(long sid) {
			this.sid = sid;
			return this;
		}

		public SingleAcidSelector category(int category) {
			this.category = category;
			return this;
		}

		public SingleAcidSelector type(int type) {
			this.type = type;
			return this;
		}

		public SingleAcidSelector level(int level) {
			this.level = level;
			return this;
		}

		public AcidRange build() {
			AcidHelper acidHelper = AcidHelper.getInstance();
			// check condition is valid
			boolean[] bs = new boolean[] { profile >= 0, category >= 0,
					type >= 0, level >= 0 };
			boolean preSet = true;
			for (int i = 0; i < bs.length; i++) {
				// raise error if some prefix has no value set,
				// but this has value set
				if (!preSet && bs[i]) {
					throw new IllegalArgumentException(
							"some value is set, but some prefix is not set");
				}
				// otherwise record it for continue
				preSet = bs[i];
			}
			long rangeFrom = acidHelper.getMaskValue(profile, category, type,
					level, sid, false);
			long rangeTo = acidHelper.getMaskValue(profile, category, type,
					level, sid, true);
			return new AcidRange(rangeFrom, rangeTo, this);
		}

		public Integer profile() {
			int v = profile;
			if (v != -1) {
				return v;
			} else {
				return null;
			}
		}

		public Long sid() {
			long v = sid;
			if (v != -1) {
				return v;
			} else {
				return null;
			}
		}

		public Integer category() {
			int v = category;
			if (v != -1) {
				return v;
			} else {
				return null;
			}
		}

		public Integer type() {
			int v = type;
			if (v != -1) {
				return v;
			} else {
				return null;
			}
		}

		public Integer level() {
			int v = level;
			if (v != -1) {
				return v;
			} else {
				return null;
			}
		}
	}

	public static final SingleAcidSelector singleSelector() {
		return new SingleAcidSelector();
	}

}

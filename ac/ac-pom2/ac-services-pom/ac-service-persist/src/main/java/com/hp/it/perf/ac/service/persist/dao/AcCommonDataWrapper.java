package com.hp.it.perf.ac.service.persist.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcContext;
import com.hp.it.perf.ac.common.model.AcidHelper;

@Document(collection = AcCommonDataWrapper.COLLECTION_PREFIX)
@TypeAlias("acd")
@CompoundIndexes({
		@CompoundIndex(def = "{\"y\" : 1, \"n\" : 1, \"cm\" : 1, \"ds\" : -1}", name = "idx_type_name_created_duration"),
		@CompoundIndex(def = "{\"n\" : 1, \"cm\" : 1, \"ds\" : -1}", name = "idx_name_created_duration"),
		@CompoundIndex(def = "{\"cm\" : 1, \"ds\" : -1}", name = "idx_created_duration") })
public class AcCommonDataWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String COLLECTION_PREFIX = "AcCommonData";

	public static class AcContextsWrapper implements Serializable {

		private static final long serialVersionUID = 1L;

		@Field("c1")
		private String context1;

		@Field("c2")
		private String context2;

		@Field("c3")
		private String context3;

		@Field("c4")
		private String context4;

		@Field("c5")
		private String context5;

		@Field("c6")
		private String context6;

		@Field("c7")
		private String context7;

		@Field("c8")
		private String context8;

		@Field("c9")
		private String context9;

		@Field("c10")
		private String context10;

		@Field("c11")
		private String context11;

		@Field("c12")
		private String context12;

		@Field("co")
		private List<AcContext> otherContexts = null;

		public String getContext1() {
			return context1;
		}

		public void setContext1(String context1) {
			this.context1 = context1;
		}

		public String getContext2() {
			return context2;
		}

		public void setContext2(String context2) {
			this.context2 = context2;
		}

		public String getContext3() {
			return context3;
		}

		public void setContext3(String context3) {
			this.context3 = context3;
		}

		public String getContext4() {
			return context4;
		}

		public void setContext4(String context4) {
			this.context4 = context4;
		}

		public String getContext5() {
			return context5;
		}

		public void setContext5(String context5) {
			this.context5 = context5;
		}

		public String getContext6() {
			return context6;
		}

		public void setContext6(String context6) {
			this.context6 = context6;
		}

		public String getContext7() {
			return context7;
		}

		public void setContext7(String context7) {
			this.context7 = context7;
		}

		public String getContext8() {
			return context8;
		}

		public void setContext8(String context8) {
			this.context8 = context8;
		}

		public String getContext9() {
			return context9;
		}

		public void setContext9(String context9) {
			this.context9 = context9;
		}

		public String getContext10() {
			return context10;
		}

		public void setContext10(String context10) {
			this.context10 = context10;
		}

		public String getContext11() {
			return context11;
		}

		public void setContext11(String context11) {
			this.context11 = context11;
		}

		public String getContext12() {
			return context12;
		}

		public void setContext12(String context12) {
			this.context12 = context12;
		}

		public List<AcContext> getOtherContexts() {
			return otherContexts;
		}

		public void setOtherContexts(List<AcContext> otherContexts) {
			this.otherContexts = otherContexts;
		}

		public static AcContextsWrapper toWrapper(List<AcContext> contexts) {
			if (contexts.isEmpty()) {
				return null;
			}
			AcContextsWrapper wrapper = new AcContextsWrapper();
			List<AcContext> others = null;
			for (AcContext context : contexts) {
				switch (context.getCode()) {
				case 1:
					wrapper.context1 = context.getValue();
					break;
				case 2:
					wrapper.context2 = context.getValue();
					break;
				case 3:
					wrapper.context3 = context.getValue();
					break;
				case 4:
					wrapper.context4 = context.getValue();
					break;
				case 5:
					wrapper.context5 = context.getValue();
					break;
				case 6:
					wrapper.context6 = context.getValue();
					break;
				case 7:
					wrapper.context7 = context.getValue();
					break;
				case 8:
					wrapper.context8 = context.getValue();
					break;
				case 9:
					wrapper.context9 = context.getValue();
					break;
				case 10:
					wrapper.context10 = context.getValue();
					break;
				case 11:
					wrapper.context11 = context.getValue();
					break;
				case 12:
					wrapper.context12 = context.getValue();
					break;
				default:
					if (others == null) {
						others = new ArrayList<AcContext>();
					}
					others.add(context);
					break;
				}
				wrapper.otherContexts = others;
			}
			return wrapper;
		}

		public static List<AcContext> toContextData(AcContextsWrapper wrapper) {
			List<AcContext> contexts = new ArrayList<AcContext>();
			if (wrapper == null) {
				return contexts;
			}
			if (wrapper.context1 != null) {
				contexts.add(new AcContext(1, wrapper.context1));
			}
			if (wrapper.context2 != null) {
				contexts.add(new AcContext(2, wrapper.context2));
			}
			if (wrapper.context3 != null) {
				contexts.add(new AcContext(3, wrapper.context3));
			}
			if (wrapper.context4 != null) {
				contexts.add(new AcContext(4, wrapper.context4));
			}
			if (wrapper.context5 != null) {
				contexts.add(new AcContext(5, wrapper.context5));
			}
			if (wrapper.context6 != null) {
				contexts.add(new AcContext(6, wrapper.context6));
			}
			if (wrapper.context7 != null) {
				contexts.add(new AcContext(7, wrapper.context7));
			}
			if (wrapper.context8 != null) {
				contexts.add(new AcContext(8, wrapper.context8));
			}
			if (wrapper.context9 != null) {
				contexts.add(new AcContext(9, wrapper.context9));
			}
			if (wrapper.context10 != null) {
				contexts.add(new AcContext(10, wrapper.context10));
			}
			if (wrapper.context11 != null) {
				contexts.add(new AcContext(11, wrapper.context11));
			}
			if (wrapper.context12 != null) {
				contexts.add(new AcContext(12, wrapper.context12));
			}
			if (wrapper.otherContexts != null) {
				contexts.addAll(wrapper.otherContexts);
			}
			return contexts;
		}

	}

	@Id
	private long acid;

	@Transient
	private transient int category;

	@Field("y")
	private int type;

	@Field("l")
	private int level;

	@Field("n")
	private String name;

	@Field("d")
	private int duration = 0;

	// duration / 1000
	@Field("ds")
	private int durationSec = 0;

	@Field("c")
	private Date created;

	// created.getTime() / (60 * 1000)
	@Field("cm")
	private int createdMin;

	@Field("r")
	private Long refAcid;

	@Field("x")
	private AcContextsWrapper context = null;

	public static AcCommonDataWrapper toWrapper(AcCommonData acCommonData) {
		AcCommonDataWrapper wrapper = new AcCommonDataWrapper();
		wrapper.setAcid(acCommonData.getAcid());
		wrapper.setCreated(acCommonData.getCreated());
		wrapper.setDuration(acCommonData.getDuration());
		wrapper.setName(acCommonData.getName());
		wrapper.setRefAcid(acCommonData.getRefAcid());
		wrapper.setContext(AcContextsWrapper.toWrapper(acCommonData
				.getContexts()));
		return wrapper;
	}

	public int getDurationSec() {
		return durationSec;
	}

	public int getCreatedMin() {
		return createdMin;
	}

	public static AcCommonData toCommonData(AcCommonDataWrapper acWrapper) {
		AcCommonData commonData = new AcCommonData();
		commonData.setAcid(acWrapper.getAcid());
		commonData.setCreated(acWrapper.getCreated());
		commonData.setDuration(acWrapper.getDuration());
		commonData.setName(acWrapper.getName());
		commonData.setRefAcid(acWrapper.getRefAcid());
		commonData.getContexts().addAll(
				AcContextsWrapper.toContextData(acWrapper.getContext()));
		return commonData;
	}

	public long getAcid() {
		return acid;
	}

	public void setAcid(long acid) {
		this.acid = acid;
		calcuateEncodedValue();
	}

	private void calcuateEncodedValue() {
		AcidHelper acidHelper = AcidHelper.getInstance();
		category = acidHelper.getCategory(acid);
		type = acidHelper.getType(acid);
		level = acidHelper.getLevel(acid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
		this.durationSec = duration / 1000;
	}

	public long getCreated() {
		return created == null ? 0L : created.getTime();
	}

	public void setCreated(long created) {
		this.created = created == 0 ? null : new Date(created);
		this.createdMin = ((int) (created / 60000));
	}

	public long getRefAcid() {
		return refAcid == null ? 0 : refAcid;
	}

	public void setRefAcid(long refAcid) {
		if (refAcid == 0) {
			this.refAcid = null;
		} else {
			this.refAcid = refAcid;
		}
	}

	public AcContextsWrapper getContext() {
		return context;
	}

	public void setContext(AcContextsWrapper context) {
		this.context = context;
	}

	public int getCategory() {
		return category;
	}

	public int getType() {
		return type;
	}

	public int getLevel() {
		return level;
	}

}

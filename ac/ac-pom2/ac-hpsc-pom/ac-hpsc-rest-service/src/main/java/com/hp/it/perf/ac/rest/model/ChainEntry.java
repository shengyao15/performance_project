package com.hp.it.perf.ac.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.hp.it.perf.ac.common.model.AcidHelper;

public class ChainEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8979748594881886460L;

	public static class EntryData implements Serializable {
		private static final long serialVersionUID = 7727505993305610681L;
		
		private long acid;
		private String name;
		private String category;
		private String type;
		private long duration;
		
		public EntryData(long acid, String name, String category, String type, long duration) {
			this.acid = acid;
			this.name = name;
			this.category = category;
			this.type = type;
			this.duration = duration;
		}
		public String getTitle() {
			return category;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		@JsonIgnore
		public long getAcid() {
			return acid;
		}
		@JsonProperty("acid")
		public String getHexAcid() {
			return AcidHelper.getInstance().toHexString(acid);
		}
		public void setAcid(long acid) {
			this.acid = acid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		
		@Override
        public String toString() {
            return "EntryData [acid=" + acid + ", name=" + name + ", category" + category + ", type=" + type + ", duration=" + duration + "]";
        }
	}
	
	private long acid;

	private String name;

	private String type;

	private long duration;
	
	private final List<EntryData> entries = new ArrayList<EntryData>();

	private final List<ChainEntry> children = new ArrayList<ChainEntry>();

	@JsonIgnore
	private ChainEntry parent;

	public List<EntryData> getEntries() {
		return entries;
	}

	@JsonIgnore
	public long getAcid() {
		return acid;
	}

	@JsonProperty("acid")
	public String getHexAcid() {
		return AcidHelper.getInstance().toHexString(acid);
	}
	
	public void setAcid(long acid) {
		this.acid = acid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ChainEntry> getChildren() {
		return children;
	}

	public ChainEntry getParent() {
		return parent;
	}

	public void setParent(ChainEntry parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer, "");
		return buffer.toString();
	}

	private void toString(StringBuffer buffer, String indent) {
		buffer.append("ChainEntry [acid=" + acid + ", name=" + name + ", type="
				+ type + ", duration=" + duration + ", entries" + entries + "]");
		indent += " ";
		for (ChainEntry ch : children) {
			buffer.append('\n');
			buffer.append(indent);
			ch.toString(buffer, indent);
		}
	}
}

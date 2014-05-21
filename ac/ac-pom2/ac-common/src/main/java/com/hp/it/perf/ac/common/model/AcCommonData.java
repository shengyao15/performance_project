package com.hp.it.perf.ac.common.model;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.persistence.Table;

//import org.springframework.data.annotation.Persistent;
//import org.springframework.data.annotation.TypeAlias;
//import org.springframework.data.mongodb.core.index.CompoundIndex;
//import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Field;

@Entity()
@Table(name = "AC_COMMON_DATA")
public class AcCommonData implements Serializable, Cloneable {

	private static final long serialVersionUID = -6297100841760372164L;

	@Id
	@Column(name = "ACID")
	@org.springframework.data.annotation.Id
	protected long acid;

	@Column(name = "NAME", length = 255)
	@Indexed(direction = IndexDirection.ASCENDING, name="name")
	protected String name;

	@Column(name = "DURATION")
	@Indexed(direction = IndexDirection.DESCENDING, name="duration")
	protected int duration = 0;

	@Column(name = "CREATED")
	@Indexed(direction = IndexDirection.ASCENDING, name="created")
	// @Field("c")
	protected long created;
	
	@Column(name = "REFACID")
	// @Field("r")
	protected long refAcid;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "AC_CONTEXT", joinColumns = @JoinColumn(name = "ACID"))
	@OrderBy("code")
	// @Field("t")
	protected List<AcContext> contexts = new ArrayList<AcContext>();

	public AcCommonData() {
	}

	public AcCommonData(AcCommonData clone) {
		this.setAcid(clone.getAcid());
		this.setCreated(clone.getCreated());
		this.setDuration(clone.getDuration());
		this.setName(clone.getName());
		this.setRefAcid(clone.getRefAcid());
		this.contexts.addAll(clone.getContexts());
	}

	@ConstructorProperties({ "acid", "name", "duration", "created", "refAcid",
			"contexts" })
	public AcCommonData(long acid, String name, int duration, long created,
			long refAcid, List<AcContext> contexts) {
		this.setAcid(acid);
		this.setCreated(created);
		this.setDuration(duration);
		this.setName(name);
		this.setRefAcid(refAcid);
		if (contexts != null) {
			this.contexts.addAll(contexts);
		}
	}

	public void setContexts(List<AcContext> contexts) {
		this.contexts = contexts;
	}

	@Override
	public Object clone() {
		AcCommonData data;
		try {
			data = (AcCommonData) super.clone();
			data.contexts = new ArrayList<AcContext>(this.contexts);
			return data;
		} catch (CloneNotSupportedException never) {
			throw new Error(never);
		}
	}

	public long getAcid() {
		return this.acid;
	}

	public int getProfile() {
		return AcidHelper.getInstance().getProfile(this.acid);
	}

	public int getCategory() {
		return AcidHelper.getInstance().getCategory(this.acid);
	}

	public long getCreated() {
		return created;
	}

	public int getDuration() {
		return duration;
	}

	public int getLevel() {
		return AcidHelper.getInstance().getLevel(this.acid);
	}

	public AcLevel getLevelValue(AcDictionary dictionary) {
		return getCategory(dictionary).level(getLevel());
	}

	public List<AcContext> getContexts() {
		return contexts;
	}

	public long getSid() {
		return AcidHelper.getInstance().getSid(this.acid);
	}

	public int getType() {
		return AcidHelper.getInstance().getType(this.acid);
	}

	public AcType getType(AcDictionary dictionary) {
		return getCategory(dictionary).type(getType());
	}

	public AcCategory getCategory(AcDictionary dictionary) {
		if (dictionary == null) {
			throw new IllegalArgumentException("null dictionary");
		}
		return dictionary.category(getCategory());
	}

	public void setAcid(long acid) {
		this.acid = acid;
	}

	public void setCreated(long time) {
		this.created = time;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getRefAcid() {
		return refAcid;
	}

	public void setRefAcid(long refAcid) {
		this.refAcid = refAcid;
	}

	@Override
	public String toString() {
		return AcidHelper.getInstance().toHexString(this.acid);
	}

	public AcContext getAcContext(AcContextType contextType) {
		for (AcContext context : contexts) {
			if (context.getCode() == contextType.code()) {
				return context;
			}
		}
		return null;
	}
}

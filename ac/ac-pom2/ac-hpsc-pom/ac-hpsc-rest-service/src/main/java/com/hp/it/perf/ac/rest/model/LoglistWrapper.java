package com.hp.it.perf.ac.rest.model;

import org.codehaus.jackson.annotate.JsonValue;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.rest.util.Utils;

public class LoglistWrapper extends AcCommonData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7578233356955397686L;

	public LoglistWrapper() {
		super();
	}

	public LoglistWrapper(AcCommonData clone) {
		this.setAcid(clone.getAcid());
		this.setCreated(clone.getCreated());
		this.setDuration(clone.getDuration());
		this.setName(clone.getName());
		this.setRefAcid(clone.getRefAcid());
		this.setContexts(clone.getContexts());
	}

	@JsonValue
	private Object[] toValue() {
		return new Object[] { AcidHelper.getInstance().toHexString(acid), name,
				duration, Utils.long2Date(created),
				getCategory(HpscDictionary.INSTANCE).name(),
				getType(HpscDictionary.INSTANCE).name(),
				getLevelValue(HpscDictionary.INSTANCE).name() };
	}
}

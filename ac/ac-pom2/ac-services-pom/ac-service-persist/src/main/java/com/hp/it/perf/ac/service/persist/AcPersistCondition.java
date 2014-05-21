package com.hp.it.perf.ac.service.persist;

import java.io.Serializable;

import com.hp.it.perf.ac.common.model.AcidSelectors.AcidRange;
import com.hp.it.perf.ac.common.realtime.TimeWindow;

public class AcPersistCondition implements Serializable {

	private static final long serialVersionUID = -8645690540862903597L;

	private TimeWindow timeWindow;
	// acid range
	private AcidRange acidRange;
	// regex like
	private String name;
	// null no order, or field name
	private String orderBy;

	// true asc, false desc
	private boolean orderByDirection = true;

	private int limitNumber = 0;

	public TimeWindow getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(TimeWindow timeWindow) {
		this.timeWindow = timeWindow;
	}

	public AcidRange getAcidRange() {
		return acidRange;
	}

	public void setAcidRange(AcidRange acidRange) {
		this.acidRange = acidRange;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public int getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(int limitNumber) {
		this.limitNumber = limitNumber;
	}

	public boolean isOrderByDirection() {
		return orderByDirection;
	}

	public void setOrderByDirection(boolean orderByDirection) {
		this.orderByDirection = orderByDirection;
	}

}

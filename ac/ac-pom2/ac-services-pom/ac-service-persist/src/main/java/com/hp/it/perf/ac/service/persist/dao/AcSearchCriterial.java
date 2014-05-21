package com.hp.it.perf.ac.service.persist.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AcSearchCriterial implements Serializable {

	public static String AC_ID = "acId";

	public static String CURRENT_PAGE = "currentPage";

	public static String MAX_COUNT_PER_PAGE = "maxCountPerPage";

	/**
     * 
     */
	private static final long serialVersionUID = 6569795054527041603L;

	private String criterialName;

	private List<Object> value = new ArrayList<Object>();

	private Operation operation = Operation.EQUAL;
	
	@SuppressWarnings("unused")
	private AcSearchCriterial() {
	}

	public AcSearchCriterial(String name, Object object) {
		this.criterialName = name;
		if (object instanceof Collection<?>) {
			this.value.addAll((Collection<?>) object);
		} else {
			this.value.add(object);
		}
	}

	public AcSearchCriterial(String name, Object object, Operation operation) {
		this.criterialName = name;
		if (object instanceof Collection<?>) {
			this.value.addAll((Collection<?>) object);
		} else {
			this.value.add(object);
		}
		this.operation = operation;
	}

	public String getCriterialName() {
		return criterialName;
	}

	public void setCriterialName(String criterialName) {
		this.criterialName = criterialName;
	}

	public List<Object> getValue() {
		return value;
	}

	public void setValue(List<Object> value) {
		this.value = value;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public static enum Operation {
		EQUAL, GREATER, LESS, BETWEEN, IN, GE, LE, LIKE
	}

}

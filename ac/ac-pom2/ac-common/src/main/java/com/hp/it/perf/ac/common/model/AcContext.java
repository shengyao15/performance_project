package com.hp.it.perf.ac.common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

//import org.springframework.data.mongodb.core.mapping.Field;

@Embeddable
public class AcContext implements Serializable {

	private static final long serialVersionUID = -1982377888658943394L;

	@Column(name = "VALUE")
	// @Field("v")
	private String value;

	@Column(name = "CODE")
	// @Field("c")
	private int code;

	public AcContext() {
	}

	public AcContext(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public AcContextType getTypeValue(AcDictionary dictionary) {
		if (dictionary == null) {
			throw new IllegalArgumentException("null dictionary");
		}
		return dictionary.contextType(getCode());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AcContext other = (AcContext) obj;
		if (code != other.code)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AcContext [code=%s, value=%s]", code, value);
	}

}

package com.hp.it.perf.ac.rest.model;

import java.io.Serializable;
import java.util.Arrays;

public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 98820949688161458L;

	private int id;

	private String value;

	private Type[] types;
	
	private Level[] levels;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Type[] getTypes() {
		return types;
	}

	public void setTypes(Type[] types) {
		this.types = types;
	}

	public Level[] getLevels() {
		return levels;
	}

	public void setLevels(Level[] levels) {
		this.levels = levels;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + Arrays.hashCode(types);
		result = prime * result + Arrays.hashCode(levels);
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
		Category other = (Category) obj;
		if (id != other.id)
			return false;
		if (!Arrays.equals(types, other.types))
			return false;
		if (!Arrays.equals(levels, other.levels))
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
		return "Category [id=" + id + ", value=" + value + ", types="
				+ Arrays.toString(types) + ", levels=" + Arrays.toString(levels) + "]";
	}
}

package com.hp.it.innovation.collaboration.dao.hibernate.orm;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class BooleanType implements UserType {

	public final static boolean DEFAULT_VALUE = false;

	public final static int[] SQL_TYPES = new int[] {Types.BIT};

	public Object assemble(Serializable cached, Object owner) {
		return cached;
	}

	public Object deepCopy(Object obj) {
		return obj;
	}

	public Serializable disassemble(Object value) {
		return (Serializable)value;
	}

	public boolean equals(Object x, Object y) {
		if (x == y) {
			return true;
		}
		else if (x == null || y == null) {
			return false;
		}
		else {
			return x.equals(y);
		}
	}

	public int hashCode(Object x) {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object obj)
		throws HibernateException, SQLException {

		Boolean value = (Boolean)Hibernate.BOOLEAN.nullSafeGet(rs, names[0]);

		if (value == null) {
			//return Boolean.valueOf(DEFAULT_VALUE);
			return null;
		}
		else {
			return value;
		}
	}

	public void nullSafeSet(PreparedStatement ps, Object obj, int index)
		throws HibernateException, SQLException {

		if (obj == null) {
			//obj = Boolean.valueOf(DEFAULT_VALUE);
		}

		Hibernate.BOOLEAN.nullSafeSet(ps, obj, index);
	}

	public Object replace(Object original, Object target, Object owner) {
		return original;
	}

	public Class<Boolean> returnedClass() {
		return Boolean.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
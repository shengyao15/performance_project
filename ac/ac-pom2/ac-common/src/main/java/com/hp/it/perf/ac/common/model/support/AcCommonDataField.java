package com.hp.it.perf.ac.common.model.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import java.util.List;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AcCommonDataField {

	// sequence is useful for reflect write/set fields
	public enum Field {
		Identifier(long.class), Category(String.class), Type(String.class), Level(
				String.class), Name(String.class), Duration(int.class), Created(
				long.class), Context(List.class), Related(Iterator.class), RefIdentifier(long.class);

		private Class<?> targetClass;

		private Field(Class<?> targetClass) {
			this.targetClass = targetClass;
		}

		public Class<?> getTargetClass() {
			return targetClass;
		}
	}

	public interface Converter {

		// not supported will throw error
		void setSource(AnnotatedElement source) throws IllegalArgumentException;

		Object convert(Object value);
	}

	Field value();

	Class<? extends Converter> converter() default Converter.class;

}

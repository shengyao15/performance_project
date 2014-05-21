package com.hp.it.perf.ac.load.parse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParserPattern {

	public @interface Parameter {

		String name() default AcTextParserConstant.KEY_DEFAULT;

		String value() default "";

		Class<?> classValue() default Object.class;

	}

	String value();

	Class<? extends AcTextParser> parser() default AcTextParser.class;

	Parameter[] parameters() default {};
}

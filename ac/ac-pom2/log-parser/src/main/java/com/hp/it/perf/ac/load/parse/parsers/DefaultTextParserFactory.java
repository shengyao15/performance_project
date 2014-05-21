package com.hp.it.perf.ac.load.parse.parsers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.ParserPattern;

public class DefaultTextParserFactory {

	private static DefaultTextParserFactory instance = new DefaultTextParserFactory();
	protected Map<Class<?>, Class<? extends AcTextParser>> mapping = new HashMap<Class<?>, Class<? extends AcTextParser>>();

	{
		addTypeMapping(String.class, EmptyTextParser.class);
		addTypeMapping(Number.class, NumberTextParser.class);
		addTypeMapping(int.class, NumberTextParser.class);
		addTypeMapping(long.class, NumberTextParser.class);
		addTypeMapping(double.class, NumberTextParser.class);
		addTypeMapping(float.class, NumberTextParser.class);
		addTypeMapping(short.class, NumberTextParser.class);
		addTypeMapping(byte.class, NumberTextParser.class);
		addTypeMapping(Date.class, DateTimeTextParser.class);
	}

	public static DefaultTextParserFactory getDefaultTextParserFactory() {
		return instance;
	}

	protected void addTypeMapping(Class<?> targetType,
			Class<? extends AcTextParser> parserType) {
		mapping.put(targetType, parserType);
	}

	public static void setDefaultTextParserFactory(
			DefaultTextParserFactory factory) {
		if (factory == null) {
			throw new IllegalArgumentException("null factory");
		}
		instance = factory;
	}

	public Class<? extends AcTextParser> getParserClassByType(Class<?> type) {
		if (type != null) {
			// check Annotation
			if (type.isAnnotationPresent(ParserPattern.class)) {
				return TypeAnnotationParser.class;
			}
			// check enum class
			if (type.isEnum()) {
				return EnumTextParser.class;
			}
		}
		Class<? extends AcTextParser> parserType = mapping.get(type);
		while (parserType == null & type != null) {
			type = type.getSuperclass();
			parserType = mapping.get(type);
		}
		return parserType;
	}

}

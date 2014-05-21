package com.hp.it.perf.ac.load.parse.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.it.perf.ac.load.bind.AcArrayBinder;
import com.hp.it.perf.ac.load.bind.AcBeanBinder;
import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcMemeberAccessor;
import com.hp.it.perf.ac.load.bind.AcObjectBinder;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcParserSetting;
import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DefaultTextParserFactory;
import com.hp.it.perf.ac.load.parse.parsers.TypeAnnotationParser;

public class TextPatternScanner extends AcProcessorConfig {

	private AcTextParserConfigBuilder parsers = new AcTextParserConfigBuilder();
	private Set<Class<?>> processingClasses = new HashSet<Class<?>>();
	private Map<String, AcTextParserConfigBuilder> derivedConfigBuilders = new HashMap<String, AcTextParserConfigBuilder>();

	public static AcTextStreamProcessor createProcessor(Class<?> bindingClass) {
		return new AcTextProcessor(new TextPatternScanner(bindingClass));
	}

	public static AcTextStreamProcessor createAutodetectProcessor(
			Class<?>... bindingClasses) {
		checkBindingClasses(bindingClasses);
		AcTextStreamProcessor[] processors = new AcTextStreamProcessor[bindingClasses.length];
		for (int i = 0; i < bindingClasses.length; i++) {
			processors[i] = createProcessor(bindingClasses[i]);
		}
		return new AutodetectStreamProcessor(processors);
	}
	
	public static AcTextPipelineParseBuilder createPipelineParseBuilder(Class<?> bindingClass) {
		return new AcTextPipelineParseBuilderImpl(new TextPatternScanner(bindingClass));
	}

	public static AcTextStreamProcessor createProcessor(
			Comparable<Object> rangeComparators, Class<?> bindingClass) {
		TextPatternScanner scanner = new TextPatternScanner(bindingClass);
		return new SortedStreamInspector(rangeComparators, scanner,
				new AcTextProcessor(scanner));
	}

	public static AcTextStreamProcessor createAutodetectProcessor(
			final Comparable<Object> rangeComparator,
			Class<?>... bindingClasses) {
		checkBindingClasses(bindingClasses);
		AcTextStreamProcessor[] processors = new AcTextStreamProcessor[bindingClasses.length];
		for (int i = 0; i < bindingClasses.length; i++) {
			processors[i] = createProcessor(rangeComparator, bindingClasses[i]);

		}
		return new AutodetectStreamProcessor(processors) {

			@Override
			protected void startDetect(AcTextStreamProcessor processor) {
				((SortedStreamInspector) processor).setInspectMode(false);
			}

			@Override
			protected void finishDetect(AcTextStreamProcessor processor) {
				((SortedStreamInspector) processor).setInspectMode(true);
			}

		};
	}
	
	public static AcTextPipelineParseBuilder createAutodetectPipelineParseBuilder(
			Class<?>... bindingClasses) {
		checkBindingClasses(bindingClasses);
		AcTextPipelineParseBuilder[] builders = new AcTextPipelineParseBuilder[bindingClasses.length];
		for (int i = 0; i < bindingClasses.length; i++) {
			builders[i] = createPipelineParseBuilder(bindingClasses[i]);

		}
		return new AutodetectPipelineParseBuilder(builders);
	}

	private static void checkBindingClasses(Class<?>... bindingClasses) {
		if (bindingClasses.length == 0) {
			throw new IllegalArgumentException(
					"expect at least 1 binding class");
		}
	}

	public TextPatternScanner(Class<?> bindingClass)
			throws AcParseSyntaxException {
		try {
			setBinder(initBindingClass(bindingClass));
			// processing parsers
			initParsers(parsers);
			setParser(parsers.getParser(getParserPattern(bindingClass).value()));
		} catch (Exception e) {
			throw new AcParseSyntaxException(
					"Parse preparation error on binding " + bindingClass, e);
		}
	}

	private void initParsers(AcTextParserConfigBuilder parsers)
			throws AcParseSyntaxException {
		for (String parserId : parsers.keys()) {
			AcTextParser parser = parsers.getParser(parserId);
			if (derivedConfigBuilders.containsKey(parserId)) {
				parser.init(parsers.createConfig(parserId,
						derivedConfigBuilders.get(parserId)));
			} else {
				parser.init(parsers.createConfig(parserId));
			}
		}
	}

	private AcBeanBinder initBindingClass(Class<?> bindingClass)
			throws AcParseSyntaxException {
		ParserPattern parserPattern = getParserPattern(bindingClass);
		if (parserPattern == null) {
			throw new AcParseSyntaxException(
					"no ParserPattern annotation presented on " + bindingClass);
		}
		processingClasses.add(bindingClass);
		AcBeanBinder constructorBinder;
		try {
			AcParserSetting parserSetting = processAnnotatedElement(
					parserPattern, bindingClass);
			if (parserSetting.getParserClass() == TypeAnnotationParser.class) {
				// parser detect loop
				throw new AcParseSyntaxException(
						"No default parser class type defined for "
								+ bindingClass);
			}
			constructorBinder = new AcBeanBinder(
					bindingClass.getDeclaredConstructor());
		} catch (SecurityException e) {
			throw new AcParseSyntaxException("cannot get constructor", e);
		} catch (NoSuchMethodException e) {
			throw new AcParseSyntaxException("cannot get constructor", e);
		}
		for (Field field : bindingClass.getDeclaredFields()) {
			ParserPattern pattern = getParserPattern(field);
			if (pattern != null) {
				processAnnotatedElement(pattern, field.getType());
				AcMemeberAccessor fieldBinder = new AcMemeberAccessor(field);
				constructorBinder.addChildBinder(pattern.value(), fieldBinder);
				// process derived parser
				AcBinder derivedBinder = processDerivedParser(pattern.value(),
						field.getType(), field.getGenericType());
				if (derivedBinder != null) {
					fieldBinder.setTypeBuilder(derivedBinder);
				}
			}
		}
		for (Method method : bindingClass.getDeclaredMethods()) {
			ParserPattern pattern = getParserPattern(method);
			if (pattern != null) {
				if (method.getParameterTypes().length == 0) {
					throw new AcParseSyntaxException(
							"method with patter 1 or more parameter " + method);
				}
				processAnnotatedElement(pattern, method.getParameterTypes()[0]);
				AcMemeberAccessor methodBinder = new AcMemeberAccessor(method);
				constructorBinder.addChildBinder(pattern.value(), methodBinder);
				// process derived parser
				// check if it is multiple arguments
				Class<?> paramType;
				if (method.getParameterTypes().length>1) {
					paramType = Object[].class;
				} else {
					paramType = method.getParameterTypes()[0];
				}
				AcBinder derivedBinder = processDerivedParser(pattern.value(),
						paramType, method.getGenericParameterTypes()[0]);
				if (derivedBinder != null) {
					methodBinder.setTypeBuilder(derivedBinder);
				}
			}
		}
		return constructorBinder;
	}

	private ParserPattern getParserPattern(AnnotatedElement annotatedElement) {
		ParserPattern annotation = annotatedElement
				.getAnnotation(ParserPattern.class);
		return annotation;
	}

	private AcBinder processDerivedParser(String name, Class<?> type,
			Type genericType) throws AcParseSyntaxException {
		if (type.isArray()) {
			// Array type
			AcBinder componentBinder = processDerivedParser(name,
					type.getComponentType(), null);
			return new AcArrayBinder(componentBinder);
		}
		if (type.isPrimitive()
				|| type.getPackage().getName().startsWith("java.")) {
			return new AcObjectBinder(type);
		}
		if (type.isEnum()) {
			return new AcObjectBinder(type);
		}
		if (processingClasses.contains(type)) {
			return null;
		}
		TextPatternScanner scanner = new TextPatternScanner(type);
		derivedConfigBuilders.put(name, scanner.getParsers());
		return scanner.getBinder();
	}

	private AcParserSetting processAnnotatedElement(ParserPattern pattern,
			Class<?> targetType) throws AcParseSyntaxException {
		AcParserSetting setting = new AcParserSetting();
		setting.setParserName(pattern.value());
		setting.setParameters(convertParameter(pattern.parameters()));
		if (pattern.parser() == AcTextParser.class) {
			// default value
			Class<? extends AcTextParser> defaultParserClass = DefaultTextParserFactory
					.getDefaultTextParserFactory().getParserClassByType(
							targetType);
			if (defaultParserClass == null) {
				throw new AcParseSyntaxException(
						"No default parser class type defined for "
								+ targetType);
			}
			setting.setParserClass(defaultParserClass);
			boolean hasKeyTypeParameter = false;
			for (ParserParameter parameter : setting.getParameters()) {
				if (AcTextParserConstant.KEY_TYPE.equals(parameter.getName())) {
					hasKeyTypeParameter = true;
					break;
				}
			}
			if (!hasKeyTypeParameter) {
				ParserParameter[] newParameters = new ParserParameter[setting
						.getParameters().length + 1];
				System.arraycopy(setting.getParameters(), 0, newParameters, 0,
						newParameters.length - 1);
				ParserParameter parameter = new ParserParameter();
				parameter.setClassValue(targetType);
				parameter.setName(AcTextParserConstant.KEY_TYPE);
				newParameters[newParameters.length - 1] = parameter;
				setting.setParameters(newParameters);
			}
		} else {
			setting.setParserClass(pattern.parser());
		}
		parsers.addParser(setting);
		return setting;
	}

	private ParserParameter[] convertParameter(Parameter[] parameters) {
		ParserParameter[] pParameters = new ParserParameter[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			ParserParameter pParameter = new ParserParameter();
			pParameter.setName(parameters[i].name());
			pParameter.setValue(parameters[i].value());
			pParameter.setClassValue(parameters[i].classValue());
			pParameters[i] = pParameter;
		}
		return pParameters;
	}

	public AcTextParserConfigBuilder getParsers() {
		return parsers;
	}

}

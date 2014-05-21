package com.hp.it.perf.ac.load.parse.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.hp.it.perf.ac.load.bind.AcChainedBinder;
import com.hp.it.perf.ac.load.bind.AcJsonArrayBinder;
import com.hp.it.perf.ac.load.bind.AcJsonObjectBinder;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcParserSetting;
import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.parsers.ConstantTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DateTimeTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.EmptyTextParser;
import com.hp.it.perf.ac.load.parse.parsers.NumberTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;

public class AcJsonProcessorConfig extends AcProcessorConfig {

	private static final Map<String, Class<? extends AcTextParser>> knownMapping = new HashMap<String, Class<? extends AcTextParser>>();
	private static final Map<String, Class<? extends AcChainedBinder>> knownBinding = new HashMap<String, Class<? extends AcChainedBinder>>();

	private static final String PARSER_TYPE = "parser";
	private static final String PARSER_CHILDREN = "children";
	private static final String PARSER_ROOT = "root";
	private static final String PARSER_ALIAS = "alias";
	private static final String DEFAULT_VALUE = String.format(
			"{\"%s\": \"%s\"}", PARSER_TYPE, "text");

	static {
		knownMapping.put("delims", DelimsTextParser.class);
		knownMapping.put("datetime", DateTimeTextParser.class);
		knownMapping.put("number", NumberTextParser.class);
		knownMapping.put("regex", RegexTextParser.class);
		knownBinding.put("regex", AcJsonArrayBinder.class);
		knownMapping.put("text", EmptyTextParser.class);
		knownMapping.put("constant", ConstantTextParser.class);
		knownMapping.put("delimslist", DelimsListTextParser.class);
		knownBinding.put("delimslist", AcJsonArrayBinder.class);
	}

	private static class ParserDef {
		private AcParserSetting setting = new AcParserSetting();
		private AcChainedBinder binder = new AcJsonObjectBinder();
		private AcTextParserConfigBuilder parsers = new AcTextParserConfigBuilder();
		private Map<String, ParserDef> children0 = new HashMap<String, AcJsonProcessorConfig.ParserDef>();
		private List<String> childNames = new ArrayList<String>();

		public ParserDef getChild(String name) {
			return children0.get(name);
		}

		public void addChild(String name, ParserDef child) {
			childNames.add(name);
			children0.put(name, child);
		}

		public int childCount() {
			return childNames.size();
		}

		public String getChildName(int index) {
			return childNames.get(index);
		}

		public AcChainedBinder getBinder() {
			return binder;
		}
	}

	public AcJsonProcessorConfig(String jsonDef, String rootElement)
			throws AcParseSyntaxException {
		try {
			JSONObject jsonObj = (JSONObject) JSONValue
					.parseWithException(jsonDef);
			ParserDef root = new ParserDef();
			setupParserSetting(jsonObj, root);
			initParsers(root);
			setParser(root.parsers.getParser(rootElement));
			setBinder(root.getChild(rootElement).getBinder());
		} catch (Exception e) {
			throw new AcParseSyntaxException(e);
		}
	}

	private void initParsers(ParserDef parserDef) throws AcParseSyntaxException {
		AcTextParserConfigBuilder parsers = parserDef.parsers;
		for (int i = 0; i < parserDef.childCount(); i++) {
			// NOTE: use list+map to enable loop with modification
			String parserId = parserDef.getChildName(i);
			ParserDef child = parserDef.getChild(parserId);
			AcTextParser parser = parsers.getParser(parserId);
			if (child.childCount() != 0) {
				initParsers(child);
				parser.init(autoCreateParser(
						parsers.createConfig(parserId, child.parsers), child));
			} else {
				parser.init(autoCreateParser(parsers.createConfig(parserId),
						child));
			}
		}
	}

	private AcTextParserConfig autoCreateParser(
			final AcTextParserConfig config, final ParserDef parent) {
		return new AcTextParserConfig() {

			@Override
			public ParserParameter[] getInitParameters(String key) {
				return config.getInitParameters(key);
			}

			@Override
			public String[] getInitParameterKeys() {
				return config.getInitParameterKeys();
			}

			@Override
			public boolean hasInitParameter(String key) {
				return config.hasInitParameter(key);
			}

			@Override
			public AcTextParser getParser(String id)
					throws AcParseSyntaxException {
				try {
					return config.getParser(id);
				} catch (AcParseSyntaxException e) {
					createParser(parent, id, JSONValue.parse(DEFAULT_VALUE));
					return config.getParser(id);
				}
			}

			@Override
			public String getName() {
				return config.getName();
			}
		};
	}

	private void setupParserSetting(JSONObject childObjs, ParserDef parent) {
		for (Object entryObj : childObjs.entrySet()) {
			String parserName = (String) ((Map.Entry<?, ?>) entryObj).getKey();
			Object value = ((Map.Entry<?, ?>) entryObj).getValue();
			createParser(parent, parserName, value);
		}
	}

	protected void createParser(ParserDef parent, String parserName,
			Object value) {
		ParserDef def = new ParserDef();
		def.setting.setParserName(parserName);
		if (!(value instanceof JSONObject)) {
			throw new IllegalArgumentException("unexpected value: " + value);
		}
		JSONObject body = (JSONObject) value;
		// process type
		Object parserType = body.remove(PARSER_TYPE);
		if (!(parserType instanceof String)) {
			throw new IllegalArgumentException("unexpected '" + PARSER_TYPE
					+ "' with value: " + parserType);
		}
		def.setting.setParserClass(getParserClass((String) parserType));
		// process children
		Object parserChildren = body.remove(PARSER_CHILDREN);
		if (parserChildren != null) {
			if (!(parserChildren instanceof JSONObject)) {
				throw new IllegalArgumentException("unexpected '"
						+ PARSER_CHILDREN + "' with value: " + parserChildren);
			} else {
				setupParserSetting((JSONObject) parserChildren, def);
			}
		}
		// process alias
		Object parserAlias = body.remove(PARSER_ALIAS);
		if (parserAlias != null) {
			if (!(parserAlias instanceof String)) {
				throw new IllegalArgumentException("unexpected '"
						+ PARSER_ALIAS + "' with value: " + parserAlias);
			}
		} else {
			parserAlias = parserName;
		}
		// process parameters (others)
		List<ParserParameter> parameterList = new ArrayList<ParserParameter>();
		for (Object parameterObj : body.entrySet()) {
			String parameterName = (String) ((Map.Entry<?, ?>) parameterObj)
					.getKey();
			Object parameterValue = ((Map.Entry<?, ?>) parameterObj).getValue();
			// only split array
			if (parameterValue instanceof JSONArray) {
				for (Object parameterValueItem : (JSONArray) parameterValue) {
					addParameterIntoList(parameterName, parameterValueItem,
							parameterList);
				}
			} else {
				addParameterIntoList(parameterName, parameterValue,
						parameterList);
			}
		}
		def.setting.setParameters(parameterList
				.toArray(new ParserParameter[parameterList.size()]));
		// setup binder
		setupBinder(def, (String) parserType);
		def.binder.setName(parserName, (String) parserAlias);
		parent.binder.addChildBinder(def.binder);
		// add into tree
		parent.addChild(parserName, def);
		parent.parsers.addParser(def.setting);
		def.parsers.setParent(parent.parsers);
	}

	private void setupBinder(ParserDef def, String parserType) {
		Class<? extends AcChainedBinder> binderClass = knownBinding
				.get(parserType);
		if (binderClass != null) {
			try {
				def.binder = binderClass.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private void addParameterIntoList(String parameterName,
			Object parameterValueItem, List<ParserParameter> parameterList) {
		if (parameterValueItem != null) {
			parameterValueItem = String.valueOf(parameterValueItem);
		}
		ParserParameter parameter = new ParserParameter();
		parameter.setName(parameterName);
		parameter.setValue((String) parameterValueItem);
		parameterList.add(parameter);
	}

	private Class<? extends AcTextParser> getParserClass(String parserType)
			throws IllegalArgumentException {
		Class<? extends AcTextParser> parserClass = knownMapping
				.get(parserType);
		if (parserClass == null) {
			throw new IllegalArgumentException("unknown parser type class: "
					+ parserType);
		}
		return parserClass;
	}

	public static AcTextStreamProcessor createProcessor(String jsonDef) {
		return new AcTextProcessor(new AcJsonProcessorConfig(jsonDef,
				PARSER_ROOT));
	}

	public static AcTextPipelineParseBuilder createPipelineBuilder(
			String jsonDef) {
		return new AcTextPipelineParseBuilderImpl(new AcJsonProcessorConfig(
				jsonDef, PARSER_ROOT));
	}

	public static AcTextStreamProcessor createProcessor(String jsonDef,
			String rootElement) {
		return new AcTextProcessor(new AcJsonProcessorConfig(jsonDef,
				rootElement));
	}

	public static AcTextPipelineParseBuilder createPipelineBuilder(
			String jsonDef, String rootElement) {
		return new AcTextPipelineParseBuilderImpl(new AcJsonProcessorConfig(
				jsonDef, rootElement));
	}

}

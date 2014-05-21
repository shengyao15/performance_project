package com.hp.it.perf.ac.load.parse.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.it.perf.ac.load.common.AcKeyValue;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcParserSetting;
import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.parsers.OneOfTextParser;

class AcTextParserConfigBuilder {

	private Map<String, AcKeyValue<AcTextParser, ParserParameter[]>> parsers = new LinkedHashMap<String, AcKeyValue<AcTextParser, ParserParameter[]>>();
	private AcTextParserConfigBuilder parent;

	protected AcTextParser getParser(String id) throws AcParseSyntaxException {
		if (parsers.containsKey(id)) {
			return parsers.get(id).getKey();
		} else if (parent != null) {
			return parent.getParser(id);
		} else {
			throw new AcParseSyntaxException("parser not defined: " + id);
		}
	}

	public void addParser(AcParserSetting parserSetting)
			throws AcParseSyntaxException {
		AcTextParser parser;
		try {
			parser = (AcTextParser) parserSetting.getParserClass()
					.newInstance();
		} catch (Exception e) {
			throw new AcParseSyntaxException("cannot create parser instance", e);
		}
		String parserName = parserSetting.getParserName();
		if (parsers.containsKey(parserName)) {
			AcKeyValue<AcTextParser, ParserParameter[]> keyValue = parsers
					.get(parserName);
			OneOfTextParser masterParser;
			if (!(keyValue.getKey() instanceof OneOfTextParser)) {
				masterParser = new OneOfTextParser();
				masterParser.addSubParser(keyValue.getKey(),
						keyValue.getValue());
				parsers.put(parserName,
						new AcKeyValue<AcTextParser, ParserParameter[]>(
								masterParser, null));
			} else {
				masterParser = (OneOfTextParser) keyValue.getKey();
			}
			masterParser.addSubParser(parser, parserSetting.getParameters());
		} else {
			parsers.put(parserName,
					new AcKeyValue<AcTextParser, ParserParameter[]>(parser,
							parserSetting.getParameters()));
		}
	}

	public AcTextParserConfig createConfig(final String id)
			throws AcParseSyntaxException {
		// check parser exits
		getParser(id);
		return new AcTextParserConfig() {

			private String[] keys;

			@Override
			public AcTextParser getParser(String id)
					throws AcParseSyntaxException {
				return AcTextParserConfigBuilder.this.getParser(id);
			}

			@Override
			public ParserParameter[] getInitParameters(String key) {
				ParserParameter[] params = parsers.get(id).getValue();
				List<ParserParameter> list = new ArrayList<ParserParameter>();
				for (ParserParameter param : params) {
					if (key.equals(param.getName())) {
						list.add(param);
					}
				}
				return list.toArray(new ParserParameter[list.size()]);
			}

			@Override
			public String[] getInitParameterKeys() {
				if (keys == null) {
					ParserParameter[] params = parsers.get(id).getValue();
					Set<String> set = new LinkedHashSet<String>();
					for (ParserParameter param : params) {
						if (!set.contains(param.getName())) {
							set.add(param.getName());
						}
					}
					keys = set.toArray(new String[set.size()]);
				}
				return keys.clone();
			}

			@Override
			public boolean hasInitParameter(String key) {
				for (String pKey : getInitParameterKeys()) {
					if (pKey.equals(key)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getName() {
				return id;
			}

		};
	}

	public String[] keys() {
		return parsers.keySet().toArray(new String[parsers.size()]);
	}

	public AcTextParserConfig createConfig(final String id,
			final AcTextParserConfigBuilder builder) {
		// check parser exits
		getParser(id);
		return new AcTextParserConfig() {

			private String[] keys;

			@Override
			public AcTextParser getParser(String id)
					throws AcParseSyntaxException {
				return builder.getParser(id);
			}

			@Override
			public ParserParameter[] getInitParameters(String key) {
				ParserParameter[] params = parsers.get(id).getValue();
				List<ParserParameter> list = new ArrayList<ParserParameter>();
				for (ParserParameter param : params) {
					if (key.equals(param.getName())) {
						list.add(param);
					}
				}
				return list.toArray(new ParserParameter[list.size()]);
			}

			@Override
			public String[] getInitParameterKeys() {
				if (keys == null) {
					ParserParameter[] params = parsers.get(id).getValue();
					Set<String> set = new LinkedHashSet<String>();
					for (ParserParameter param : params) {
						if (!set.contains(param.getName())) {
							set.add(param.getName());
						}
					}
					keys = set.toArray(new String[set.size()]);
				}
				return keys.clone();
			}

			@Override
			public boolean hasInitParameter(String key) {
				for (String pKey : getInitParameterKeys()) {
					if (pKey.equals(key)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getName() {
				return id;
			}

		};
	}

	void setParent(AcTextParserConfigBuilder parent) {
		this.parent = parent;
	}

}

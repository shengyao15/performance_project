package com.hp.it.perf.ac.load.parse;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Properties;

public class AcTextParserContext {
	private Properties properties = new Properties();

	private Map<AcTextParser, Map<Object, Object>> storeSpace = new IdentityHashMap<AcTextParser, Map<Object, Object>>();

	private Map<Object, Object> attributes = new HashMap<Object, Object>();

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public void setProperties(Properties prop) {
		properties.putAll(prop);
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public String getProperty(String key, String def) {
		return properties.getProperty(key, def);
	}

	public void setParserAttribute(AcTextParser parser, Object key, Object value) {
		Map<Object, Object> map = storeSpace.get(parser);
		if (map == null) {
			map = new HashMap<Object, Object>();
			storeSpace.put(parser, map);
		}
		map.put(key, value);
	}

	public Object getParserAttribute(AcTextParser parser, Object key) {
		Map<Object, Object> map = storeSpace.get(parser);
		return map == null ? null : map.get(key);
	}

	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}

	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	public void removeAttribute(Object key) {
		attributes.remove(key);
	}

}

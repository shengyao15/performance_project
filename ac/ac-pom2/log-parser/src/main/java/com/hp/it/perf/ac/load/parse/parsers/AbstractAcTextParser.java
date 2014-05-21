package com.hp.it.perf.ac.load.parse.parsers;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public abstract class AbstractAcTextParser implements AcTextParser,
		AcTextParserConstant {

	private Boolean throwIfError = null;

	private static final Object CACHE_KEY = new Object();

	protected String name;

	private static class LRUMap extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = -4355663349659954778L;

		private int limit;

		public LRUMap(int limit) {
			this.limit = limit;
		}

		@Override
		protected boolean removeEldestEntry(Entry<String, Object> eldest) {
			return size() > limit;
		}

	}

	@Override
	public void init(AcTextParserConfig config) throws AcParseSyntaxException {
		name = config.getName();
	}

	@Override
	public void setErrorMode(boolean throwIfError) {
		this.throwIfError = throwIfError ? Boolean.TRUE : Boolean.FALSE;
	}

	protected String getInitParameter(AcTextParserConfig config, String key) {
		if (config.hasInitParameter(key)) {
			return config.getInitParameters(key)[0].getValue();
		} else {
			return null;
		}
	}

	protected String[] getInitParameters(AcTextParserConfig config, String key) {
		ParserParameter[] parameters;
		if (config.hasInitParameter(key)) {
			parameters = config.getInitParameters(key);
		} else {
			return new String[0];
		}
		String[] values = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			values[i] = parameters[i].getValue();
		}
		return values;
	}

	protected String getDefaultInitParameter(AcTextParserConfig config,
			String key) {
		if (config.hasInitParameter(key)) {
			return config.getInitParameters(key)[0].getValue();
		} else if (config.hasInitParameter(AcTextParserConstant.KEY_DEFAULT)) {
			return config.getInitParameters(AcTextParserConstant.KEY_DEFAULT)[0]
					.getValue();
		} else {
			return null;
		}
	}

	protected boolean hasDefaultInitParameter(AcTextParserConfig config,
			String key) {
		if (config.hasInitParameter(key)) {
			return true;
		} else if (config.hasInitParameter(AcTextParserConstant.KEY_DEFAULT)) {
			return true;
		} else {
			return false;
		}
	}

	protected String[] getDefaultInitParameters(AcTextParserConfig config,
			String key) {
		ParserParameter[] parameters;
		if (config.hasInitParameter(key)) {
			parameters = config.getInitParameters(key);
		} else if (config.hasInitParameter(AcTextParserConstant.KEY_DEFAULT)) {
			parameters = config
					.getInitParameters(AcTextParserConstant.KEY_DEFAULT);
		} else {
			return null;
		}
		String[] values = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			values[i] = parameters[i].getValue();
		}
		return values;
	}

	protected Class<?>[] getDefaultClassInitParameters(
			AcTextParserConfig config, String key) {
		ParserParameter[] parameters;
		if (config.hasInitParameter(key)) {
			parameters = config.getInitParameters(key);
		} else if (config.hasInitParameter(AcTextParserConstant.KEY_DEFAULT)) {
			parameters = config
					.getInitParameters(AcTextParserConstant.KEY_DEFAULT);
		} else {
			return null;
		}
		Class<?>[] values = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			values[i] = parameters[i].getClassValue();
		}
		return values;
	}

	protected Class<?> getDefaultClassInitParameter(AcTextParserConfig config,
			String key) {
		ParserParameter parameter;
		if (config.hasInitParameter(key)) {
			parameter = config.getInitParameters(key)[0];
		} else if (config.hasInitParameter(AcTextParserConstant.KEY_DEFAULT)) {
			parameter = config
					.getInitParameters(AcTextParserConstant.KEY_DEFAULT)[0];
		} else {
			return null;
		}
		return parameter.getClassValue();
	}

	protected Class<?> getInitClassParameter(AcTextParserConfig config,
			String key) {
		if (config.hasInitParameter(key)) {
			ParserParameter parameter = config.getInitParameters(key)[0];
			return parameter.getClassValue();
		} else {
			return null;
		}
	}

	protected AcTextParseErrorResult createParseError(AcParseException e,
			AcTextParserContext context) throws AcParseException {
		if (isThrowError(context)) {
			throw e;
		} else {
			return new AcTextParseErrorResult(e);
		}
	}

	protected AcTextParseErrorResult createParseError(String msg,
			AcTextParserContext context) throws AcParseException {
		if (isThrowError(context)) {
			throw new AcParseException(msg);
		} else {
			AcTextParseErrorResult errorResult = new AcTextParseErrorResult();
			errorResult.setMessage(msg);
			return errorResult;
		}
	}

	protected AcTextParseErrorResult createParseError(String msg,
			Throwable cause, AcTextParserContext context)
			throws AcParseException {
		if (isThrowError(context)) {
			throw new AcParseException(msg, cause);
		} else {
			AcTextParseErrorResult errorResult = new AcTextParseErrorResult();
			errorResult.setMessage(msg);
			errorResult.setCause(cause);
			return errorResult;
		}
	}

	protected AcTextParseErrorResult createParseInsufficientError(String msg,
			AcTextParserContext context, int expected)
			throws AcParseInsufficientException {
		if (isThrowError(context)) {
			AcParseInsufficientException apie = new AcParseInsufficientException(
					msg);
			apie.setExpected(expected);
			throw apie;
		} else {
			AcTextParseErrorResult errorResult = new AcTextParseErrorResult();
			errorResult.setMessage(msg);
			errorResult.setInsufficientError(true);
			return errorResult;
		}
	}

	protected AcTextParseErrorResult createParseError(
			AcTextParseErrorResult errorResult, AcTextParserContext context)
			throws AcParseException {
		if (isThrowError(context)) {
			throw errorResult.createParseError();
		} else {
			return errorResult;
		}
	}

	protected int getExpected(AcParseInsufficientException apie,
			AcTextParseErrorResult child) {
		if (apie != null) {
			return apie.getExpected();
		} else {
			return child.getInsufficentExpected();
		}
	}

	private boolean isThrowError(AcTextParserContext context) {
		// throw if error flag is lasy loaded from context, and saved into
		// parser
		if (throwIfError == null) {
			throwIfError = Boolean.parseBoolean(context.getProperty(
					AcTextParserConstant.PROPERTY_ERRORMODE, "false"));
		}
		return throwIfError.booleanValue();
	}

	protected void setParserContextAttribute(AcTextParserContext context,
			Object key, Object value) {
		context.setParserAttribute(this, key, value);
	}

	protected Object getParserContextAttribute(AcTextParserContext context,
			Object key) {
		return context.getParserAttribute(this, key);
	}

	protected void saveCachedResult(AcTextParserContext context, String text,
			Object result) {
		LRUMap cacheLRU = (LRUMap) context.getParserAttribute(this, CACHE_KEY);
		if (cacheLRU == null) {
			cacheLRU = new LRUMap(16);
			context.setParserAttribute(this, CACHE_KEY, cacheLRU);
		}
	}

	protected Object getCachedResult(AcTextParserContext context, String text) {
		LRUMap cacheLRU = (LRUMap) context.getParserAttribute(this, CACHE_KEY);
		return cacheLRU == null ? null : cacheLRU.get(text);
	}

	protected boolean hasCachedResult(AcTextParserContext context, String text) {
		LRUMap cacheLRU = (LRUMap) context.getParserAttribute(this, CACHE_KEY);
		return cacheLRU == null ? false : cacheLRU.containsKey(text);
	}

}

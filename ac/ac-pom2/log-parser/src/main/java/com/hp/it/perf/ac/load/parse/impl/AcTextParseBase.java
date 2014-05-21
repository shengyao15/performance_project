package com.hp.it.perf.ac.load.parse.impl;

import java.util.Properties;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.plugins.AcParsePluginException;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;

class AcTextParseBase {

	private Properties properties;

	private AcTextProcessPlugin[] plugins;

	public void setProcessProperties(Properties properties) {
		this.properties = properties;
	}

	public void setPluginManager(AcTextProcessPluginManager manager) {
		if (manager != null) {
			this.plugins = manager.getPlugins();
		} else {
			this.plugins = null;
		}
	}

	protected Object processPluginResolve(Object bean, AcContentLine line,
			AcTextParserContext context) throws AcStopParseException {
		for (int i = 0, n = plugins.length; i < n && bean != null; i++) {
			try {
				bean = plugins[i].processResolve(bean, line, context);
			} catch (AcParsePluginException e) {
				if (e.getCause() instanceof AcStopParseException) {
					throw (AcStopParseException) e.getCause();
				}
				// otherwise, ignore it
			}
		}
		return bean;
	}

	protected AcContentLine processPluginRead(AcContentLine line,
			AcTextParserContext context) throws AcStopParseException {
		for (int i = 0, n = plugins.length; i < n && line != null; i++) {
			try {
				line = plugins[i].processRead(line, context);
			} catch (AcParsePluginException e) {
				if (e.getCause() instanceof AcStopParseException) {
					throw (AcStopParseException) e.getCause();
				}
				// otherwise, ignore it
			}
		}
		return line;
	}

	protected <T extends AcContent<?>> T processPluginStart(T content,
			AcTextParserContext context) throws AcStopParseException {
		for (int i = 0, n = plugins.length; i < n; i++) {
			try {
				content = plugins[i].processStart(content, context);
				if (content == null) {
					throw new IllegalArgumentException(
							"plugin create null content is not allowed: "
									+ plugins[i]);
				}
			} catch (AcParsePluginException e) {
				if (e.getCause() instanceof AcStopParseException) {
					throw (AcStopParseException) e.getCause();
				}
				// otherwise, ignore it
			}
		}
		return content;
	}

	protected void processPluginEnd(AcTextParserContext context) {
		for (int i = 0, n = plugins.length; i < n; i++) {
			try {
				plugins[i].processEnd(context);
			} catch (AcParsePluginException ignored) {
			}
		}
	}

	protected <T extends AcLoadException> T processPluginParseError(T error,
			AcContentLine line, AcTextParserContext context)
			throws AcStopParseException {
		for (int i = 0, n = plugins.length; i < n && error != null; i++) {
			try {
				error = plugins[i].processParseError(error, line, context);
			} catch (AcParsePluginException e) {
				if (e.getCause() instanceof AcStopParseException) {
					throw (AcStopParseException) e.getCause();
				}
				// otherwise, ignore it
			}
		}
		return error;
	}

	protected <T extends Throwable> T processPluginGeneralError(T error,
			AcTextParserContext context) {
		for (int i = 0, n = plugins.length; i < n && error != null; i++) {
			try {
				error = plugins[i].processGeneralError(error, context);
			} catch (AcParsePluginException ignored) {
			}
		}
		return error;
	}

	protected AcTextParserContext initParseContext() {
		if (plugins == null) {
			this.plugins = AcTextProcessPluginManager.getDefaultManager()
					.getPlugins();
		}
		AcTextParserContext context = new AcTextParserContext();
		if (properties != null) {
			context.setProperties(properties);
		}
		return context;
	}

}

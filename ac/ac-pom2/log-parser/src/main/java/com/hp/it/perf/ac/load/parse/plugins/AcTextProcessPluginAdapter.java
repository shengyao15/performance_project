package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public abstract class AcTextProcessPluginAdapter implements AcTextProcessPlugin {

	@Override
	public <T extends AcContent<?>> T processStart(T content, AcTextParserContext context)
			throws AcParsePluginException {
		return content;
	}

	@Override
	public Object processResolve(Object bean, AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException {
		return bean;
	}

	@Override
	public void processEnd(AcTextParserContext context)
			throws AcParsePluginException {
	}

	@Override
	public <T extends Throwable> T processParseError(T throwable,
			AcContentLine line, AcTextParserContext context)
			throws AcParsePluginException {
		return throwable;
	}

	@Override
	public <T extends Throwable> T processGeneralError(T throwable,
			AcTextParserContext context) throws AcParsePluginException {
		return throwable;
	}

	@Override
	public AcContentLine processRead(AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException {
		return line;
	}

}

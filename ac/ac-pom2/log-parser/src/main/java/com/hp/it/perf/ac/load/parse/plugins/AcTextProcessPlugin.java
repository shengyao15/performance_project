package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public interface AcTextProcessPlugin {

	public <T extends AcContent<?>> T processStart(T content, AcTextParserContext context)
			throws AcParsePluginException;

	public <T extends Throwable> T processParseError(T throwable,
			AcContentLine line, AcTextParserContext context)
			throws AcParsePluginException;

	public <T extends Throwable> T processGeneralError(T throwable,
			AcTextParserContext context) throws AcParsePluginException;

	public Object processResolve(Object bean, AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException;

	public AcContentLine processRead(AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException;

	public void processEnd(AcTextParserContext context)
			throws AcParsePluginException;

}

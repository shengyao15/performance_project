package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public class AcProcessErrorCheckPlugin extends AcTextProcessPluginAdapter {

	private int maxError;

	public AcProcessErrorCheckPlugin(int maxError) {
		this.maxError = maxError;
	}

	@Override
	public <T extends AcContent<?>> T processStart(T content,
			AcTextParserContext context) throws AcParsePluginException {
		context.setAttribute(this, new int[1]);
		return content;
	}

	@Override
	public Object processResolve(Object bean, AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException {
		int[] counts = (int[]) context.getAttribute(this);
		if (counts != null) {
			context.removeAttribute(this);
		}
		return bean;
	}

	@Override
	public <T extends Throwable> T processParseError(T throwable,
			AcContentLine line, AcTextParserContext context)
			throws AcParsePluginException {
		int[] counts = (int[]) context.getAttribute(this);
		if (counts != null) {
			counts[0]++;
			if (counts[0] > maxError) {
				throw new AcParsePluginException(new AcStopParseException(
						"exceed error: " + maxError));
			}
		}
		return throwable;
	}

}

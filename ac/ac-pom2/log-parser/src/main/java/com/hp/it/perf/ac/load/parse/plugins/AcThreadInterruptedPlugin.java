package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public class AcThreadInterruptedPlugin extends AcTextProcessPluginAdapter {

	@Override
	public AcContentLine processRead(AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException {
		if (Thread.interrupted()) {
			context.setAttribute(this, this);
			throw new AcParsePluginException("thread interrupted",
					new AcStopParseException("thread interrupted")
							.setNormalStop(false));
		} else {
			return line;
		}
	}

	@Override
	public <T extends Throwable> T processGeneralError(T throwable,
			AcTextParserContext context) throws AcParsePluginException {
		if (context.getAttribute(this) == this) {
			// interrupt current thread again
			Thread.currentThread().interrupt();
		}
		return throwable;
	}

}

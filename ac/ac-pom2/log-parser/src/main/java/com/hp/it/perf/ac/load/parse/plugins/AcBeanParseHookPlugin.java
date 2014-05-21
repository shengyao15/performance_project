package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public class AcBeanParseHookPlugin extends AcTextProcessPluginAdapter implements
		AcTextProcessPlugin {

	@Override
	public Object processResolve(Object bean, AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException {
		if (bean instanceof AcBeanParseHook) {
			try {
				((AcBeanParseHook) bean).onReady(line, context);
			} catch (Exception e) {
				throw new AcParsePluginException("bean parse hook get error", e);
			}
		}
		return bean;
	}

}

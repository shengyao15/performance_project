package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public interface AcBeanParseHook {

	public void onReady(AcContentLine contentLine, AcTextParserContext context);

}

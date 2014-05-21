package com.hp.it.perf.ac.load.parse;

import java.util.Properties;

import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;

public interface AcTextPipelineParseBuilder {

	public void setProcessProperties(Properties properties);

	public void setPluginManager(AcTextProcessPluginManager manager);

	public AcTextPipeline createPipeline(
			AcPredicate<? super AcContentLine> filter,
			AcContentHandler contentHandler);

}

package com.hp.it.perf.ac.load.parse;

import java.util.Iterator;
import java.util.Properties;

import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcContentErrorHandler;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;

public interface AcTextStreamProcessor {
	
	public void setProcessProperties(Properties properties);
	
	public void setPluginManager(AcTextProcessPluginManager manager);

	public void process(AcReaderContent content, AcPredicate<? super AcContentLine> filter,
			AcContentHandler contentHandler) throws AcLoadException;

	public Iterator<Object> iterator(AcReaderContent content, AcPredicate<? super AcContentLine> filter,
			AcContentErrorHandler errorHandler) throws AcLoadException;
	
}

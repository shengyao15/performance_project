package com.hp.it.perf.ac.load.parse;

import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;

public interface AcTextParserConfig {
	
	public String getName();

	public ParserParameter[] getInitParameters(String key);

	public String[] getInitParameterKeys();

	public boolean hasInitParameter(String key);

	public AcTextParser getParser(String id) throws AcParseSyntaxException;

}

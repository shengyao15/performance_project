package com.hp.it.perf.ac.load.parse;

public interface AcTextParser {

	public void init(AcTextParserConfig config) throws AcParseSyntaxException;

	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseInsufficientException, AcParseException;

	public boolean test(String textFragement, AcTextParserContext context);

	public void setErrorMode(boolean throwIfError);

}

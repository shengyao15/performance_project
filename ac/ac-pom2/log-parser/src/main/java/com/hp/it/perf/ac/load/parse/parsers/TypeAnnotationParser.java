package com.hp.it.perf.ac.load.parse.parsers;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;

public class TypeAnnotationParser extends AbstractAcTextParser implements
		AcTextParser {

	private AcTextParser nextParser;

	@Override
	public void init(AcTextParserConfig config) throws AcParseSyntaxException {
		Class<?> targetClass = getDefaultClassInitParameter(config, KEY_TYPE);
		if (targetClass == null) {
			throw new AcParseSyntaxException("No paraemter defined");
		}
		if (!targetClass.isAnnotationPresent(ParserPattern.class)) {
			throw new AcParseSyntaxException(targetClass
					+ " has no annotation " + ParserPattern.class.getName());
		}
		TextPatternScanner patternScanner = new TextPatternScanner(targetClass);
		nextParser = patternScanner.getParser();
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseInsufficientException, AcParseException {
		return nextParser.parse(text, context);
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		return nextParser.test(textFragement, context);
	}

	@Override
	public void setErrorMode(boolean throwIfError) {
		super.setErrorMode(throwIfError);
		nextParser.setErrorMode(throwIfError);
	}

}

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

public class ChoiceTextParser extends AbstractAcTextParser implements
		AcTextParser {

	protected AcTextParser[] subParsers;

	@Override
	public void init(AcTextParserConfig config) throws AcParseSyntaxException {
		if (!hasDefaultInitParameter(config, KEY_TYPE)) {
			throw new AcParseSyntaxException("require parameters for "
					+ KEY_TYPE);
		}
		Class<?>[] classInitParameters = getDefaultClassInitParameters(config,
				KEY_TYPE);
		subParsers = new AcTextParser[classInitParameters.length];
		int index = 0;
		for (Class<?> targetClass : classInitParameters) {
			if (!targetClass.isAnnotationPresent(ParserPattern.class)) {
				throw new AcParseSyntaxException(targetClass
						+ " has no annotation " + ParserPattern.class.getName());
			}
			TextPatternScanner patternScanner = new TextPatternScanner(
					targetClass);
			AcTextParser subParser = patternScanner.getParser();
			subParsers[index++] = subParser;
		}
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseInsufficientException, AcParseException {
		AcParseInsufficientException apie = null;
		AcParseException ape = null;
		AcTextParseErrorResult errorResult = null;
		for (int i = 0, n = subParsers.length; i < n; i++) {
			AcTextParser subParser = subParsers[i];
			AcTextParseResult parseResult;
			try {
				parseResult = subParser.parse(text, context);
				if (parseResult.hasError()) {
					errorResult = (AcTextParseErrorResult) parseResult;
				} else {
					return parseResult;
				}
			} catch (AcParseInsufficientException e) {
				apie = e;
			} catch (AcParseException e) {
				ape = e;
			}
		}
		if (errorResult != null) {
			return createParseError(errorResult, context);
		}
		if (apie != null) {
			return createParseError(apie, context);
		}
		if (ape != null) {
			return createParseError(ape, context);
		}
		return createParseError("no expected parser", context);
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		for (int i = 0, n = subParsers.length; i < n; i++) {
			AcTextParser subParser = subParsers[i];
			if (subParser.test(textFragement, context)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setErrorMode(boolean throwIfError) {
		super.setErrorMode(throwIfError);
		for (AcTextParser subParser : subParsers) {
			subParser.setErrorMode(throwIfError);
		}
	}

}

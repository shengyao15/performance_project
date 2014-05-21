package com.hp.it.perf.ac.load.parse.parsers;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;

public class MultipleDelimsTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private DelimsTextParser[] parsers;

	@Override
	public void init(final AcTextParserConfig config)
			throws AcParseSyntaxException {
		super.init(config);
		List<DelimsTextParser> list = new ArrayList<DelimsTextParser>();
		String key = AcTextParserConstant.KEY_PATTERN;
		if (!config.hasInitParameter(key)) {
			key = AcTextParserConstant.KEY_DEFAULT;
		}
		for (final ParserParameter parameter : config.getInitParameters(key)) {
			DelimsTextParser parser = new DelimsTextParser();
			parser.init(new AcTextParserConfig() {
				@Override
				public AcTextParser getParser(String id)
						throws AcParseSyntaxException {
					return config.getParser(id);
				}

				@Override
				public ParserParameter[] getInitParameters(String key) {
					if (hasInitParameter(key)) {
						return new ParserParameter[] { parameter };
					} else {
						return new ParserParameter[0];
					}
				}

				@Override
				public String[] getInitParameterKeys() {
					return new String[] { AcTextParserConstant.KEY_PATTERN };
				}

				@Override
				public boolean hasInitParameter(String key) {
					return AcTextParserConstant.KEY_PATTERN.equals(key);
				}

				@Override
				public String getName() {
					return MultipleDelimsTextParser.this.name;
				}
			});
			list.add(parser);
		}
		parsers = list.toArray(new DelimsTextParser[list.size()]);
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseInsufficientException, AcParseException {
		AcParseInsufficientException apie = null;
		AcParseException ape = null;
		AcTextParseErrorResult errorResult = null;
		String content = text.getContent();
		for (int i = 0, n = parsers.length; i < n; i++) {
			AcTextParser parser = parsers[i];
			if (parser.test(content, context)) {
				try {
					AcTextParseResult result = parser.parse(text, context);
					// try again if it is error
					if (result.hasError()) {
						errorResult = (AcTextParseErrorResult) result;
					} else {
						return result;
					}
				} catch (AcParseInsufficientException e) {
					apie = e;
				} catch (AcParseException e) {
					ape = e;
				}
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
		for (int i = 0, n = parsers.length; i < n; i++) {
			AcTextParser parser = parsers[i];
			if (parser.test(textFragement, context)) {
				return true;
			}
		}
		return false;
	}

}

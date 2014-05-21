package com.hp.it.perf.ac.load.parse.parsers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.hp.it.perf.ac.load.common.AcKeyValue;
import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcParserSetting;
import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.OneOfTextElement;

public class OneOfTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private List<AcKeyValue<AcTextParser, ParserParameter[]>> parsers = new ArrayList<AcKeyValue<AcTextParser, ParserParameter[]>>();
	private AcTextParser[] subParsers;

	public void addSubParser(AcTextParser parser, ParserParameter[] setting) {
		parsers.add(new AcKeyValue<AcTextParser, AcParserSetting.ParserParameter[]>(
				parser, setting));
	}

	@Override
	public void init(final AcTextParserConfig config)
			throws AcParseSyntaxException {
		super.init(config);
		int index = 0;
		subParsers = new AcTextParser[parsers.size()];
		for (AcKeyValue<AcTextParser, ParserParameter[]> entry : parsers) {
			final ParserParameter[] parserParameters = entry.getValue();
			entry.getKey().init(new AcTextParserConfig() {

				@Override
				public boolean hasInitParameter(String key) {
					for (ParserParameter parameter : parserParameters) {
						if (parameter.getName().equals(key)) {
							return true;
						}
					}
					return false;
				}

				@Override
				public AcTextParser getParser(String id)
						throws AcParseSyntaxException {
					return config.getParser(id);
				}

				@Override
				public ParserParameter[] getInitParameters(String key) {
					List<ParserParameter> parameters = new ArrayList<ParserParameter>();
					for (ParserParameter parameter : parserParameters) {
						if (parameter.getName().equals(key)) {
							parameters.add(parameter);
						}
					}
					return parameters.toArray(new ParserParameter[parameters
							.size()]);
				}

				@Override
				public String[] getInitParameterKeys() {
					Set<String> keys = new LinkedHashSet<String>();
					for (ParserParameter parameter : parserParameters) {
						keys.add(parameter.getName());
					}
					return keys.toArray(new String[keys.size()]);
				}

				@Override
				public String getName() {
					return OneOfTextParser.this.name;
				}
			});
			subParsers[index++] = entry.getKey();
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
					AcTextParseResult newResult = new AcTextParseResult();
					newResult.setElement(new OneOfTextElement(name, i,
							parseResult.getElement()));
					newResult.setExactMatch(parseResult.isExactMatch());
					newResult.setSource(parseResult.getSource());
					return newResult;
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

package com.hp.it.perf.ac.load.parse.parsers;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcNumberElement;

public class NumberTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private static int LONG_MAX_LENGTH = String.valueOf(Long.MAX_VALUE)
			.length() - 1;

	private static int INT_MAX_LENGTH = String.valueOf(Integer.MAX_VALUE)
			.length() - 1;

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseException {
		String content = text.getContent();
		Object cachedResult = getCachedResult(context, content);
		Boolean hasDigital = null;
		if (cachedResult instanceof Number) {
			return new AcTextParseResult(new AcNumberElement(name,
					(Number) cachedResult), content);
		} else if (cachedResult instanceof Boolean) {
			hasDigital = (Boolean) cachedResult;
		}
		if (Boolean.FALSE.equals(hasDigital) || !test(content, context)) {
			return createParseError("not number: " + content, context);
		}
		try {
			Number number = parseNumber(content);
			saveCachedResult(context, content, number);
			return new AcTextParseResult(new AcNumberElement(name, number),
					content);
		} catch (NumberFormatException e) {
			return createParseError("parse number error: " + content, e,
					context);
		}
	}

	private Number parseNumber(String content) {
		if (content.indexOf('.') == -1 && content.indexOf('E') == -1
				&& content.indexOf('e') == -1
				&& content.length() <= LONG_MAX_LENGTH) {
			return content.length() <= INT_MAX_LENGTH ? Integer
					.parseInt(content) : Long.parseLong(content);
		} else if (content.indexOf(',') != -1) {
			return Double.valueOf(content.replaceAll(",", ""));
		} else {
			return Double.valueOf(content);
		}
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		boolean hasDigital = false;
		for (char c : textFragement.toCharArray()) {
			switch (c) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
			case '+':
			case '-':
			case ',':
				// TODO
			case ' ':
				hasDigital = true;
				continue;
			}
			hasDigital = false;
			break;
		}
		saveCachedResult(context, textFragement, hasDigital);
		return hasDigital;
	}

}

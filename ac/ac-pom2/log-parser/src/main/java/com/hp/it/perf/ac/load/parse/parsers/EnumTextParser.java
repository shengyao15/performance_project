package com.hp.it.perf.ac.load.parse.parsers;

import java.util.HashMap;
import java.util.Map;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcObjectElement;

public class EnumTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private Map<String, Object> constants = new HashMap<String, Object>();
	private String[] constantStrings;
	private Class<?> enumClass;

	@Override
	public void init(AcTextParserConfig config) throws AcParseSyntaxException {
		super.init(config);
		Class<?> enumParameter = getDefaultClassInitParameter(config, KEY_TYPE);
		if (enumParameter == null) {
			throw new AcParseSyntaxException("expect parameter: " + KEY_TYPE);
		}
		if (!enumParameter.isEnum()) {
			throw new AcParseSyntaxException("expect enum class: "
					+ enumParameter);
		}
		this.enumClass = enumParameter;
		Object[] enumConstants = enumParameter.getEnumConstants();
		this.constantStrings = new String[enumConstants.length];
		int index = 0;
		for (Object enumeration : enumConstants) {
			String enumName = ((Enum<?>) enumeration).name();
			constantStrings[index++] = enumName;
			constants.put(enumName, enumeration);
		}
		ConstantTextParser.sortStringByLength(constantStrings);
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseInsufficientException, AcParseException {
		String content = text.getContent();
		Object enumeration = constants.get(content);
		if (enumeration != null) {
			return new AcTextParseResult(
					new AcObjectElement(name, enumeration), content);
		} else {
			int expected = ConstantTextParser.testExpectMore(constantStrings,
					content);
			if (expected >= 0) {
				return createParseInsufficientError("need more input", context,
						expected);
			} else {
				return createParseError(
						"unexpected enum (" + enumClass.getName() + "): \""
								+ content + "\"", context);
			}
		}
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		if (constants.containsKey(textFragement)) {
			return true;
		} else {
			return ConstantTextParser.testExpectMore(constantStrings,
					textFragement) != -1;
		}
	}

}

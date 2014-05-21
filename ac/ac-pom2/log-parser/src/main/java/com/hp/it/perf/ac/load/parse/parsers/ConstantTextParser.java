package com.hp.it.perf.ac.load.parse.parsers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcObjectElement;

public class ConstantTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private Set<String> constants;
	private String[] constantStrings;

	private static class StringLenComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			int x = o1.length();
			int y = o2.length();
			return (x == y) ? 0 : ((x < y) ? -1 : 1);
		}

	}

	@Override
	public void init(AcTextParserConfig config) {
		super.init(config);
		String[] constantParameters = getDefaultInitParameters(config,
				AcTextParserConstant.KEY_NAME);
		if (constantParameters == null) {
			throw new AcParseSyntaxException("no "
					+ AcTextParserConstant.KEY_NAME + " defined");
		}
		constants = new HashSet<String>(constantParameters.length);
		int index = 0;
		constantStrings = new String[constantParameters.length];
		for (String text : constantParameters) {
			constants.add(text);
			constantStrings[index++] = text;
		}
		sortStringByLength(constantStrings);
	}

	static void sortStringByLength(String[] stringArray) {
		Arrays.sort(stringArray, new StringLenComparator());
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseInsufficientException, AcParseException {
		String content = text.getContent();
		if (constants.contains(content)) {
			return new AcTextParseResult(new AcObjectElement(name, content),
					content);
		} else {
			int expected = testExpectMore(constantStrings, content);
			if (expected >= 0) {
				return createParseInsufficientError("need more input", context,
						expected);
			} else {
				return createParseError("unexpected constant: \"" + content
						+ "\"", context);
			}
		}
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		if (constants.contains(textFragement)) {
			return true;
		} else {
			return testExpectMore(constantStrings, textFragement) != -1;
		}
	}

	// string array should be sorted by length
	static int testExpectMore(String[] stringArray, String text) {
		for (int i = 0, n = stringArray.length; i < n; i++) {
			String string = stringArray[i];
			if (string.startsWith(text)) {
				return string.length() - text.length();
			}
		}
		return -1;
	}

}

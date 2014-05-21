package com.hp.it.perf.ac.load.parse.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcMapElement;
import com.hp.it.perf.ac.load.parse.element.AcObjectElement;
import com.hp.it.perf.ac.load.parse.element.AcRegexElement;

/* Useful page http://myregexp.com/ */
public class RegexTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private Pattern pattern;

	private List<String> subparserNames = new ArrayList<String>();
	private List<AcTextParser> subparsers = new ArrayList<AcTextParser>();

	@Override
	public void init(AcTextParserConfig config) throws AcParseSyntaxException {
		super.init(config);
		if (!hasDefaultInitParameter(config, AcTextParserConstant.KEY_PATTERN)) {
			throw new AcParseSyntaxException("expect parameters: "
					+ AcTextParserConstant.KEY_PATTERN);
		}
		try {
			pattern = Pattern.compile(getDefaultInitParameter(config,
					AcTextParserConstant.KEY_PATTERN));
		} catch (Exception e) {
			throw new AcParseSyntaxException(e);
		}
		for (String name : getInitParameters(config,
				AcTextParserConstant.KEY_NAME)) {
			subparserNames.add(name);
			subparsers.add(config.getParser(name));
		}
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseInsufficientException, AcParseException {
		String content = text.getContent();
		Matcher matcher = (Matcher) getCachedResult(context, content);
		if (matcher == null) {
			matcher = pattern.matcher(content);
		}
		if (matcher.matches()) {
			if (matcher.groupCount() > 0) {
				int subparsercount = subparsers.size();
				if (subparsercount == 0) {
					// just wrap as result
					return new AcTextParseResult(new AcRegexElement(name,
							matcher), content, isExactMatch(matcher));
				} else {
					AcMapElement rootResult = new AcMapElement(name,
							matcher.groupCount());
					boolean exactMatch = isExactMatch(matcher);
					// move to sub parsers
					for (int i = 0; i < subparsercount; i++) {
						String groupText = matcher.group(i + 1);
						if (groupText == null)
							continue;
						AcTextParser subParser = subparsers.get(i);
						AcTextToken subtext = new AcTextToken();
						subtext.setContent(groupText);
						subtext.setNextToken(content.substring(matcher
								.end(i + 1)));
						subtext.setEndOfLine(text.isEndOfLine()
								&& matcher.end(i + 1) == content.length());
						AcTextParseResult result;
						try {
							result = subParser.parse(subtext, context);
						} catch (AcParseInsufficientException e) {
							if (matcher.end(i + 1) == content.length()) {
								// may require more from external
								return createParseError(e, context);
							} else {
								return createParseError("parse error", e,
										context);
							}
						}
						exactMatch &= result.isExactMatch();
						rootResult.addChild(i, subparserNames.get(i),
								result.getElement());
					}
					return new AcTextParseResult(rootResult, content,
							exactMatch);
				}
			} else {
				return new AcTextParseResult(
						new AcObjectElement(name, content), content,
						isExactMatch(matcher));
			}
		} else if (isPartialMatch(matcher, content)) {
			return createParseInsufficientError(
					"expert more input: " + content, context, 0);
		} else {
			return createParseError("unmatched text: '" + content
					+ "', with regex: '" + pattern + "'", context);
		}
	}

	private boolean isExactMatch(Matcher matcher) {
		return matcher.hitEnd() && matcher.requireEnd();
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		Matcher matcher = pattern.matcher(textFragement);
		saveCachedResult(context, textFragement, matcher);
		return matcher.lookingAt() || isPartialMatch(matcher, textFragement);
	}

	private boolean isPartialMatch(Matcher matcher, String text) {
		// partial match:
		// http://stackoverflow.com/questions/2469231/how-can-i-perform-a-partial-match-with-java-util-regex
		// and not consider empty string
		return !matcher.matches() && matcher.hitEnd() && text.length() > 0;
	}

}

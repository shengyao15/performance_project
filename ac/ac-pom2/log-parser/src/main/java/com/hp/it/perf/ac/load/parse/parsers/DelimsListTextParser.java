package com.hp.it.perf.ac.load.parse.parsers;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcArrayElement;

public class DelimsListTextParser extends AbstractAcTextParser implements
		AcTextParser {

	public static final String DELIMS = "delims";

	private DelimiterElement delimiter;
	private AcTextParser nextParser;

	@Override
	public void init(AcTextParserConfig config) throws AcParseSyntaxException {
		super.init(config);
		if (!hasDefaultInitParameter(config, DELIMS)) {
			throw new AcParseSyntaxException("expect parameter: " + DELIMS);
		}
		this.delimiter = new DelimiterElement(getDefaultInitParameter(config,
				DELIMS));
		if (config.hasInitParameter(AcTextParserConstant.KEY_PATTERN)) {
			this.nextParser = config.getParser(getInitParameter(config,
					AcTextParserConstant.KEY_PATTERN));
		} else if (config.hasInitParameter("1")) {
			// FIXME
			// just handle legacy parameter
			this.nextParser = config.getParser(getInitParameter(config, "1"));
		} else {
			this.nextParser = new EmptyTextParser();
			this.nextParser.init(config);
		}
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseException {
		String content = text.getContent();
		int offset = 0;
		boolean tailExactMatch = false;
		AcArrayElement root = new AcArrayElement(name);
		if (content.length() == 0) {
			// empty
			return new AcTextParseResult(root, content, tailExactMatch);
		}
		int startOffset = offset;
		while (true) {
			TextMatchResult matchResult = delimiter.match(content, startOffset,
					content.length());
			AcTextToken token = new AcTextToken();
			if (matchResult != null) {
				// try parse this content
				token.setContent(content.substring(startOffset, matchResult.start()));
				token.setEndOfLine(false);
				token.setNextToken(matchResult.matched());
			} else {
				// last element or single
				token.setContent(content.substring(startOffset));
				token.setEndOfLine(text.isEndOfLine());
				token.setNextToken(text.getNextToken());
			}
			AcTextParseResult child = null;
			AcParseInsufficientException apie = null;
			try {
				child = nextParser.parse(token, context);
			} catch (AcParseInsufficientException e) {
				apie = e;
			}
			if (apie != null
					|| (child != null && child.hasError() && ((AcTextParseErrorResult) child)
							.isInsufficientError())) {
				// try read more
				if (matchResult != null) {
					startOffset = matchResult.start() + 1;
					continue;
				} else {
					// re-throw or return same error/result
					if (apie != null) {
						return createParseError(apie, context);
					} else {
						return createParseError((AcTextParseErrorResult) child,
								context);
					}
				}
			}
			root.addChild(child.getElement());
			tailExactMatch = child.isExactMatch();
			if (matchResult != null && matchResult.start() >= startOffset) {
				// try parse this content
				startOffset = matchResult.end();
			} else {
				// last element or single
				break;
			}
		}
		return new AcTextParseResult(root, content, tailExactMatch);
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		int offset = 0;
		if (textFragement.length() == 0) {
			// empty
			return true;
		}
		TextMatchResult matchResult = delimiter.match(textFragement, offset,
				textFragement.length());
		if (matchResult != null) {
			// try test this content
			return nextParser.test(
					textFragement.substring(offset, matchResult.start()),
					context);
		} else {
			// last element or single
			return nextParser.test(textFragement.substring(offset), context);
		}
	}

}

package com.hp.it.perf.ac.load.parse.parsers;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.PatternSyntaxException;

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

public class DelimsTextParser extends AbstractAcTextParser implements
		AcTextParser {

	public static final String SPACE_AS_BLANK = "space";

	private TextElement[] elements;

	public void init(AcTextParserConfig config) throws AcParseSyntaxException {
		super.init(config);
		String pattern = getDefaultInitParameter(config,
				AcTextParserConstant.KEY_PATTERN);
		if (pattern == null) {
			throw new AcParseSyntaxException("no "
					+ AcTextParserConstant.KEY_PATTERN + " defined");
		}
		try {
			preparePattern(pattern);
		} catch (PatternSyntaxException e) {
			throw new AcParseSyntaxException(e);
		}
		for (TextElement element : elements) {
			if (element instanceof PatternElement) {
				if (!isIgnoredPattern(element)) {
					((PatternElement) element).setParser(config
							.getParser(element.textValue()));
				}
			}
		}
	}

	private static class TextPart {
		StringBuilder text = new StringBuilder();
		final TextElement element;

		public TextPart(TextElement element) {
			this.element = element;
		}

		public void append(char c) {
			text.append(c);
		}

		public int length() {
			return text.length();
		}

		public String getText() {
			return text.toString();
		}
	}

	private void preparePattern(String text) {
		Stack<TextPart> stack = new Stack<TextPart>();
		DelimiterElement delimiterElement = new DelimiterElement();
		delimiterElement.setStart();
		stack.add(new TextPart(delimiterElement));
		boolean escapeChar = false;
		for (int offset = 0; offset < text.length(); offset++) {
			char c = text.charAt(offset);
			if (escapeChar) {
				switch (c) {
				case '\\':
				case '{':
				case '}':
					escapeChar = false;
					TextPart textPart = stack.peek();
					textPart.append(c);
					if (textPart.element instanceof DelimiterElement) {
						break;
					}
				default:
					throw new PatternSyntaxException("unexpected escape char '"
							+ c + "'", text, offset);
				}
			} else if (c == '\\') {
				// escape char
				escapeChar = true;
			} else if (c == '{') {
				if (stack.peek().element instanceof PatternElement) {
					throw new PatternSyntaxException("unexpected '{'", text,
							offset);
				} else {
					stack.add(new TextPart(new PatternElement()));
				}
			} else if (c == '}') {
				if (stack.peek().element instanceof PatternElement) {
					TextPart part = stack.peek();
					if (part.length() == 0) {
						throw new PatternSyntaxException("unexpected '}'",
								text, offset);
					} else {
						stack.add(new TextPart(new DelimiterElement()));
					}
				} else {
					throw new PatternSyntaxException("unexpected '}'", text,
							offset);
				}
			} else {
				stack.peek().append(c);
			}
		}
		if (!(stack.peek().element instanceof DelimiterElement)) {
			stack.add(new TextPart(new DelimiterElement()));
		}
		((DelimiterElement) stack.peek().element).setEnd();
		elements = new TextElement[stack.size()];
		for (int i = 0; i < stack.size(); i++) {
			TextPart textPart = stack.get(i);
			textPart.element.initText(textPart.getText());
			elements[i] = textPart.element;
		}
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseException, AcParseInsufficientException {
		String content = text.getContent();
		int offset = 0;
		int elementSize = elements.length;
		AcMapElement root = new AcMapElement(name, elementSize);
		int[] positions = new int[elementSize];
		int[] maxOffsets = new int[elementSize];
		TextMatchResult[] results = new TextMatchResult[elementSize];
		boolean[] exactMatches = new boolean[elementSize];
		// if end delimiter contains non blank chars, set full matched if
		// success
		boolean endDelimiterExactMatch = ((DelimiterElement) elements[elementSize - 1])
				.getMinLength() > 0;
		boolean lastPatternExactMatch = false;
		LOOP: for (int i = 0; i < elementSize; i++) {
			positions[i] = offset;
			TextElement element = elements[i];
			if (element instanceof DelimiterElement) {
				TextMatchResult matchResult = ((DelimiterElement) element)
						.matchBegin(content, offset, content.length(), 
								results[i] == null ? 0
										: (results[i].end() + 1));
				if (matchResult == null) {
					PatternSyntaxException e = new PatternSyntaxException(
							"unexpected delimiter", content, offset);
					return createParseError("parse error " + text
							+ ", expect delimiter '" + element + "' but get "
							+ e.getMessage(), e, context);
				} else {
					results[i] = matchResult;
					offset = matchResult.end();
				}
			} else if (element instanceof PatternElement) {
				DelimiterElement nextDelimiter = (DelimiterElement) elements[++i];
				int startOffset = Math.max(offset, maxOffsets[i - 1]);
				while (true) {
					TextMatchResult matchResult = nextDelimiter.match(content,
							startOffset, content.length(), results[i] == null ? 0
									: (results[i].end() + 1));
					if (matchResult != null) {
						results[i] = matchResult;
						// try parse this content
						AcTextToken token = new AcTextToken();
						token.setContent(content.substring(offset,
								matchResult.start()));
						token.setEndOfLine(nextDelimiter.isEnd()
								&& text.isEndOfLine());
						if (nextDelimiter.isEnd()) {
							// end of line
							token.setNextToken(text.getNextToken());
						} else {
							token.setNextToken(matchResult.matched());
						}
						AcTextParseResult child = null;
						AcParseInsufficientException apie = null;
						AcParseException ape = null;
						try {
							child = ((PatternElement) element).parse(token,
									context);
						} catch (AcParseInsufficientException e) {
							apie = e;
						} catch (AcParseException e) {
							ape = e;
						}
						// handle parse insufficient error
						if (apie != null
								|| (child != null && child.hasError() && ((AcTextParseErrorResult) child)
										.isInsufficientError())) {
							// parse failure, try more
							int nextMinLen = nextDelimiter.getMinLength();
							if (nextDelimiter.isEnd() && nextMinLen == 0) {
								// no more read
								// re-throw or return parse error/result
								if (apie != null) {
									return createParseError(apie, context);
								} else {
									return createParseError(
											(AcTextParseErrorResult) child,
											context);
								}
							} else if (nextMinLen == 0) {
								// no explicit delimiter, try one char more
								int expected = getExpected(apie,
										(AcTextParseErrorResult) child);
								if (expected > 0) {
									nextMinLen += expected;
								} else {
									nextMinLen++;
								}
							}
							startOffset = matchResult.start() + nextMinLen;
							results[i] = null;
							continue;
						}
						// handle parse error
						if (ape != null
								|| (child != null && child.hasError() && !((AcTextParseErrorResult) child)
										.isInsufficientError())) {
							// check if it contains previous delimiter
							DelimiterElement preElement = (DelimiterElement) elements[i - 2];
							if (preElement.getMinLength() > 0
									&& !preElement.isStart()) {
								if (preElement.match(token.getContent(), 0,
										token.getContent().length()) != null
										|| !exactMatches[i - 3]) {
									// rollback to previous one
									i -= 4;
									maxOffsets[i + 1] = offset;
									offset = positions[i + 1];
									Arrays.fill(results, i + 2, results.length,
											null);
									continue LOOP;
								}
							}
							if (ape != null) {
								return createParseError(ape, context);
							} else {
								return createParseError(
										(AcTextParseErrorResult) child, context);
							}
						}
						exactMatches[i - 1] = child.isExactMatch();
						lastPatternExactMatch = exactMatches[i - 1];
						if (!isIgnoredPattern(element)) {
							root.addChild(i - 1, element.textValue(),
									child.getElement());
						}
						positions[i] = matchResult.start();
						offset = matchResult.end();
						break;
					} else {
						// not found
						// check if previous delimiter is consume more
						DelimiterElement preElement = (DelimiterElement) elements[i - 2];
						TextMatchResult preMatchResult = results[i - 2];
						if (preElement.matchBegin(preMatchResult.matched(),
								0, preMatchResult.matched().length() - 1) != null) {
							// rollback to previous one
							i -= 4;
							offset = positions[i + 1];
							Arrays.fill(results, i + 3, results.length,
									null);
							continue LOOP;
						}
						// check if element test is success (partial success)
						if (((PatternElement) element).test(
								content.substring(offset), context)) {
							return createParseInsufficientError(
									nextDelimiter.getNotFoundMessage(),
									context, 0);
						} else {
							return createParseError(
									nextDelimiter.getNotFoundMessage(), context);
						}
					}
				}
			}
		}
		return new AcTextParseResult(root, content.substring(0, offset),
				endDelimiterExactMatch || lastPatternExactMatch);
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		int offset = 0;
		for (int i = 0; i < elements.length; i++) {
			TextElement element = elements[i];
			if (element instanceof DelimiterElement) {
				TextMatchResult matchResult = ((DelimiterElement) element)
						.matchBegin(textFragement, offset,
								textFragement.length());
				if (matchResult == null) {
					return false;
				} else {
					offset = matchResult.end();
				}
			} else if (element instanceof PatternElement) {
				DelimiterElement nextDelimiter = (DelimiterElement) elements[++i];
				TextMatchResult matchResult = nextDelimiter.match(
						textFragement, offset, textFragement.length());
				if (matchResult != null) {
					// try parse this content
					return ((PatternElement) element).test(textFragement
							.substring(offset, matchResult.start()), context);
				} else {
					// not found
					return false;
				}
			}
		}
		return true;
	}

	private boolean isIgnoredPattern(TextElement element) {
		return element instanceof PatternElement
				&& element.textValue().startsWith(":");
	}
}

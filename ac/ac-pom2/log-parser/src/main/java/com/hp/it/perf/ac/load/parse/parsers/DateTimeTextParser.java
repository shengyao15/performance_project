package com.hp.it.perf.ac.load.parse.parsers;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcObjectElement;
import com.hp.it.perf.ac.load.util.FastSimpleDateFormat;

public class DateTimeTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private static Set<String> availableFormat = new LinkedHashSet<String>();

	static {
		addDefaultDateFormat(((SimpleDateFormat) DateFormat
				.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM))
				.toPattern());
		addDefaultDateFormat("MM/dd/yyyy, HH:mm:ss");
		addDefaultDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		addDefaultDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		addDefaultDateFormat("yyyy-MM-dd HH:mm:ss");
		addDefaultDateFormat("EEE MMM d HH:mm:ss z yyyy");
	}

	private SimpleDateFormat definedFormat;
	// default time zone
	private TimeZone timezone = TimeZone.getTimeZone("GMT");

	public static void addDefaultDateFormat(String pattern)
			throws IllegalArgumentException, NullPointerException {
		// test format
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		availableFormat.add(dateFormat.toPattern());
	}

	@Override
	public void init(AcTextParserConfig config) {
		super.init(config);
		if (hasDefaultInitParameter(config, AcTextParserConstant.KEY_FORMAT)) {
			definedFormat = newDateFormat(getDefaultInitParameter(config,
					AcTextParserConstant.KEY_FORMAT));
			definedFormat.setTimeZone(timezone);
		}
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext context)
			throws AcParseException {
		String content = text.getContent();
		checkCurrentFormat(context, content);
		SimpleDateFormat currentFormat = getCurrentFormat(context);
		if (currentFormat == null) {
			return createParseError(
					"No format detected (try to use explict date format): "
							+ text.toString(), context);
		}
		ParsePosition pos;
		Date parsedDate;
		if (hasCachedResult(context, content)) {
			Object[] data = (Object[]) getCachedResult(context, content);
			if (data == null) {
				// tested failure
				parsedDate = null;
				pos = null;
			} else {
				parsedDate = (Date) data[0];
				pos = (ParsePosition) data[1];
			}
		} else {
			pos = new ParsePosition(0);
			parsedDate = parseDate(currentFormat, content, pos);
		}
		if (parsedDate != null) {
			// check if all consumed
			if (pos.getIndex() == content.length()) {
				return new AcTextParseResult(new AcObjectElement(name,
						parsedDate), content);
			} else {
				return createParseError(text.toString(), context);
			}
		} else {
			// check partial match
			if (pos != null && pos.getErrorIndex() == content.length()) {
				return createParseInsufficientError("need more for parse",
						context, 0);
			} else {
				// parse failure
				return createParseError(
						"Unparseable date: \"" + content
								+ "\", expect date time pattern: \""
								+ currentFormat.toPattern() + "\"", context);
			}
		}
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		checkCurrentFormat(context, textFragement);
		SimpleDateFormat currentFormat = getCurrentFormat(context);
		if (currentFormat == null) {
			return false;
		}
		ParsePosition pos = new ParsePosition(0);
		Date parsedDate = parseDate(currentFormat, textFragement, pos);
		if (parsedDate != null) {
			saveCachedResult(context, textFragement, new Object[] {
					textFragement, pos });
		}
		if (parsedDate == null) {
			if (pos.getErrorIndex() == textFragement.length()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	protected Date parseDate(SimpleDateFormat format, String textFragement,
			ParsePosition pos) {
		Date parsedDate = format.parse(textFragement, pos);
		// if (parsedDate != null) {
		// // NOTE: check if there is space before date (if pattern has no)
		// // because simple date format may ignore space in ahead
		// // but this is not our expected
		// String pattern = format.toPattern();
		// for (int i = 0; i < pattern.length(); i++) {
		// if (textFragement.charAt(i) == ' ') {
		// if (pattern.charAt(i) != ' ') {
		// return null;
		// }
		// } else {
		// return parsedDate;
		// }
		// }
		// }
		return parsedDate;
	}

	private void checkCurrentFormat(AcTextParserContext context, String content) {
		SimpleDateFormat currentFormat = getCurrentFormat(context);
		if (currentFormat != null)
			return;
		synchronized (this) {
			String[] formats;
			synchronized (DateTimeTextParser.class) {
				formats = availableFormat.toArray(new String[availableFormat
						.size()]);
			}
			for (String format : formats) {
				SimpleDateFormat dateFormat = newDateFormat(format);
				ParsePosition pos = new ParsePosition(0);
				if (parseDate(dateFormat, content, pos) != null
						&& pos.getIndex() == content.length()) {
					dateFormat.setTimeZone(timezone);
					setCurrentFormat(context, dateFormat);
					return;
				}
			}
		}
	}

	private SimpleDateFormat getCurrentFormat(AcTextParserContext context) {
		return definedFormat != null ? definedFormat
				: (SimpleDateFormat) getParserContextAttribute(context, this);
	}

	private void setCurrentFormat(AcTextParserContext context,
			SimpleDateFormat currentFormat) {
		setParserContextAttribute(context, this, currentFormat);
	}

	private static SimpleDateFormat newDateFormat(String pattern) {
		return new FastSimpleDateFormat(pattern);
	}

}

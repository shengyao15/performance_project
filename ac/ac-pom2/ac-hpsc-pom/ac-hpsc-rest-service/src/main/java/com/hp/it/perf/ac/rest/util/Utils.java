package com.hp.it.perf.ac.rest.util;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition;
import com.hp.it.perf.ac.common.realtime.GranularityType;
import com.hp.it.perf.ac.rest.exceptions.ConflictException;
import com.hp.it.perf.ac.rest.json.JsonUtils;

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
		protected synchronized DateFormat initialValue() {
			DateFormat df = new SimpleDateFormat(Constant.PATTERN_DATE_TIME);
			df.setTimeZone(TimeZone.getTimeZone(Constant.TIMEZONE_UTC));
			return df;
		}
	};

	public static DateFormat getDateFormat() {
		return (DateFormat) threadLocal.get();
	}

	/**
	 * Convert a string to a date.
	 * 
	 * @param source
	 *            The string need to be converted
	 * @param pattern
	 *            The pattern of the string
	 * @param zone
	 *            The time zone of the string
	 * @return date (null <code>source</code> or <code>patten</code> is null or
	 *         blank)
	 * @throws ParseException
	 * 
	 * @see {@link SimpleDateFormat#parse(String)}
	 * 
	 */
	public static Date string2Date(String source, String pattern, TimeZone zone)
			throws ParseException {
		if (StringUtils.isBlank(source) || StringUtils.isBlank(pattern)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (zone != null)
			sdf.setTimeZone(zone);
		return sdf.parse(source);
	}

	public static Date string2Date(String source) throws ParseException {
		if (StringUtils.isBlank(source))
			return null;
		return getDateFormat().parse(source);
	}

	public static Date long2Date(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTimeInMillis(time);
		return calendar.getTime();
	}

	/**
	 * Convert a date to a string.
	 * 
	 * @param date
	 *            The string need to be format
	 * @param pattern
	 *            The pattern of the string
	 * 
	 * @param zone
	 *            The time zone of the string
	 * @return string (null <code>date</code> or <code>patten</code> is null or
	 *         blank)
	 * 
	 * @see {@link SimpleDateFormat#format(Date)}
	 * 
	 */
	public static String date2String(Date date, String pattern, TimeZone zone) {
		if (date == null || StringUtils.isBlank(pattern)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (zone != null)
			sdf.setTimeZone(zone);
		return sdf.format(date);
	}

	public static String date2String(Date date) {
		if (date == null)
			return null;
		return getDateFormat().format(date);
	}

	public static QueryCondition getQueryCondition(String startTime,
			String endTime) {
		QueryCondition queryCondition = new QueryCondition();
		Calendar sCalendar = Calendar.getInstance(TimeZone
				.getTimeZone(Constant.TIMEZONE_UTC));
		Calendar eCalendar = Calendar.getInstance(TimeZone
				.getTimeZone(Constant.TIMEZONE_UTC));

		// check end time first
		if (StringUtils.isBlank(endTime)
				|| endTime.equalsIgnoreCase(Constant.NULL)) {
			// if end time is empty, set to current system date
			eCalendar.setTime(new Date());
		} else {
			try {
				eCalendar.setTime(string2Date(endTime));
			} catch (ParseException e) {
				log.error("{} is not valid data. ", endTime);
				throw new ConflictException(
						"startTime or endTime is not valid data. ", e);
			}
		}

		if (StringUtils.isBlank(startTime)
				|| startTime.equalsIgnoreCase(Constant.NULL)) {
			// set start time to just before 2 hours of the end time
			sCalendar.setTime(eCalendar.getTime());
			sCalendar.add(Calendar.HOUR_OF_DAY, -2);
		} else {
			try {
				sCalendar.setTime(string2Date(startTime));
			} catch (ParseException e) {
				log.error("{} is not valid data. ", startTime);
				throw new ConflictException(
						"startTime or endTime is not valid data. ", e);
			}
		}

		QueryCondition.TimeWindow tw = queryCondition.new TimeWindow();
		tw.setStartTime(sCalendar.getTime());
		tw.setEndTime(eCalendar.getTime());
		queryCondition.setTimeWindow(tw);
		return queryCondition;
	}

	/**
	 * Convert a POJO to a JSON string.
	 * 
	 * @param obj
	 *            The Object need to be converted
	 * @return String : JSON string, not typical key-pair format, just contains
	 *         the value for each attribute in the Object.
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * 
	 */
	public static String object2Json(Object obj)
			throws JsonGenerationException, JsonMappingException, IOException {
		if (obj == null) {
			return null;
		}

		StringWriter sw = new StringWriter();
		ObjectWriter writer = JsonUtils.getMapperInstance(false)
				.writerWithType(obj.getClass());
		writer.writeValue(sw, obj);
		return sw.toString();
	}

	public static long getCurrentDate(GranularityType granularity) {
		Calendar calendar = Calendar.getInstance(TimeZone
				.getTimeZone(Constant.TIMEZONE_UTC));
		calendar.setTime(new Date());
		// the real time has 5 minutes delay
		calendar.add(Calendar.MINUTE, -5);
		return calendar.getTimeInMillis() / granularity.getMilSecondTime()
				* granularity.getMilSecondTime();
	}

	public static String getCurrentDateString(GranularityType granularity) {
		return long2String(getCurrentDate(granularity));
	}

	public static String long2String(long time) {
		return date2String(long2Date(time));
	}

	public static long getCurrentDate(GranularityType granularity, String date) {
		if (date == null)
			return getCurrentDate(granularity);
		Calendar calendar = Calendar.getInstance(TimeZone
				.getTimeZone(Constant.TIMEZONE_UTC));
		try {
			calendar.setTime(string2Date(date));
		} catch (ParseException e) {
			log.error("{} is not valid date. ", date);
			throw new ConflictException("startTime is not valid date. ", e);
		}
		return calendar.getTimeInMillis() / granularity.getMilSecondTime()
				* granularity.getMilSecondTime();
	}

	public static long calculateDuration(long now, int granularity,
			int pageSize, boolean decrease) {
		Calendar calendar = Calendar.getInstance(TimeZone
				.getTimeZone(Constant.TIMEZONE_UTC));
		calendar.setTimeInMillis(now);
		switch (granularity) {
		case 1:
			calendar.add(Calendar.MINUTE, (decrease ? -1 : +1) * (pageSize - 1));
			break; // minute, 1
		case 2:
			calendar.add(Calendar.MINUTE, (decrease ? -10 : +10)
					* (pageSize - 1));
			break; // minute, 10
		case 3:
			calendar.add(Calendar.MINUTE, (decrease ? -15 : +15)
					* (pageSize - 1));
			break;// minute, 15
		case 4:
			calendar.add(Calendar.MINUTE, (decrease ? -30 : +30)
					* (pageSize - 1));
			break;// minute, 30
		case 5:
			calendar.add(Calendar.HOUR_OF_DAY, (decrease ? -1 : +1)
					* (pageSize - 1));
			break;// hour, 1
		case 6:
			calendar.add(Calendar.DAY_OF_MONTH, (decrease ? -1 : +1)
					* (pageSize - 1));
			break; // day, 1
		}
		return calendar.getTimeInMillis();
	}
}

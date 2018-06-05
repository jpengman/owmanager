package se.anviken.owmanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	public static String getTimeOfDay(Date date) {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
		String time = localDateFormat.format(date);
		return time;
	}

	public static String getDateAndTime(Date date) {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String time = localDateFormat.format(date);
		return time;
	}

	public static Calendar getCalendarFromShortDate(String date) throws ParseException {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(localDateFormat.parse(date).getTime());
		return cal;
	}
}

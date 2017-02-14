package se.anviken.owmanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	public static String getTimeOfDay(Date date) {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
		String time = localDateFormat.format(date);
		return time;
	}
}

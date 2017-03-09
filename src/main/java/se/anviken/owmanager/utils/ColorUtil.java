package se.anviken.owmanager.utils;

public class ColorUtil {
	public static String shaderBYR(double temp, double lowtemp, double midtemp, double hightemp) {
		long r = 0;
		long g = 0;
		long b = 0;
		double calctemp;
		if (temp <= midtemp) {
			calctemp = (temp - lowtemp) * (256 / (midtemp - lowtemp));
			r = 0 + Math.round(calctemp);
			g = 0 + Math.round(calctemp);
			b = 256 - Math.round(calctemp);
		} else {
			calctemp = (temp - midtemp) * (256 / (hightemp - midtemp));
			r = 256;
			g = 256 - Math.round(calctemp);
			b = 0;
		}
		return r + "," + g + "," + b;
	}

	public static String shaderBGYR(double temp, double lowtemp, double targettemp, double hightemp,
			double highesttemp) {
		long r = 0;
		long g = 0;
		long b = 0;
		double calctemp;
		if (temp <= targettemp) {
			calctemp = (temp - lowtemp) * (256 / (targettemp - lowtemp));
			g = 0 + Math.round(calctemp);
			b = 256 - Math.round(calctemp);
		} else if (temp <= hightemp) {
			calctemp = (temp - targettemp) * (256 / (hightemp - targettemp));
			r = 0 + Math.round(calctemp);
			g = 256;
		} else {
			calctemp = (temp - hightemp) * (256 / (highesttemp - hightemp));
			r = 256;
			g = 256 - Math.round(calctemp);
		}
		return r + "," + g + "," + b;
	}

}

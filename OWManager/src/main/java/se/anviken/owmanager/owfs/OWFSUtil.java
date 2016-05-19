package se.anviken.owmanager.owfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class OWFSUtil {
	private static final String BASE_OWHTTPD_URL = "http://localhost:2121/text";

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonForSensors() throws IOException, JSONException {
		return readJsonFromUrl(BASE_OWHTTPD_URL);
	}

	public static JSONObject readJsonForSensor(String sensorAdress) throws IOException, JSONException {
		return readJsonFromUrl(BASE_OWHTTPD_URL+"/"+sensorAdress);
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static List<String> getAllSensorAdresses() throws IOException, JSONException {
		JSONObject json = readJsonForSensors();
		Iterator<String> keys = json.keys();
		List<String> sensors = new ArrayList<String>();
		while (keys.hasNext()) {
			String temp = keys.next();
			if (temp.startsWith("28.")) {
				sensors.add(temp);
			}
		}
		return sensors;
	}
	
	public static void main(String[] args) throws IOException, JSONException {
		OWSensor sensor = new OWSensor("28.5E2F3E040000");
		System.out.println(sensor.toString());
		
	}
}
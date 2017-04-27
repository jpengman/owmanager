package se.anviken.owmanager.svg;

import java.util.ArrayList;
import java.util.List;

public class LinearGradient implements Definition, Gradient {

	private String id;

	public void setId(String id) {
		this.id = id;
	}

	public String getX1() {
		return x1;
	}

	public void setX1(String x1) {
		this.x1 = x1;
	}

	public String getY1() {
		return y1;
	}

	public void setY1(String y1) {
		this.y1 = y1;
	}

	public String getX2() {
		return x2;
	}

	public void setX2(String x2) {
		this.x2 = x2;
	}

	public String getY2() {
		return y2;
	}

	public void setY2(String y2) {
		this.y2 = y2;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}

	private String x1;
	private String y1;
	private String x2;
	private String y2;
	private List<Stop> stops = new ArrayList<Stop>();

	public void addStop(Stop stop) {
		stops.add(stop);
	}

	@Override
	public String toString() {
		String stopTags = "";
		for (Stop s : stops) {
			stopTags += s.toString();
		}
		return "<linearGradient id='" + id + "' x1='" + x1 + "' y1='" + y1 + "' x2='" + x2 + "' y2='" + y2 + "'>\n"
				+ stopTags + "</linearGradient>";
	}

	@Override
	public String toString(boolean dynamic) {
		if (dynamic) {
			return toString();// TODO implement dynamic
		} else {
			return toString();
		}
	}

	public LinearGradient(String id, String x1, String y1, String x2, String y2, List<Stop> stops) {
		super();
		this.id = id;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.stops = stops;
	}

	public LinearGradient(String id, String x1, String y1, String x2, String y2) {
		super();
		this.id = id;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public String getId() {
		return id;
	}
}

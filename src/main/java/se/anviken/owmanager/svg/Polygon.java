package se.anviken.owmanager.svg;

import java.util.ArrayList;
import java.util.List;

public class Polygon extends BasicShape implements Shape {

	private List<Point> points = new ArrayList<Point>();

	public Polygon(String pointsString) {
		String[] pointStringList = pointsString.split(" ");
		for (String pointString : pointStringList) {
			points.add(new Point(pointString));
		}
	}

	@Override
	public String toString() {
		return "\n <polygon points='" + getPointsString() + "' " + getBasicShapeAttribute() + "/>";
	}

	private String getPointsString() {
		StringBuilder sb = new StringBuilder();
		for (Point point : points) {
			if (sb.length() != 0) {
				sb.append(" ");
			}
			sb.append(point.getX() + "," + point.getY());
		}
		return sb.toString();
	}

	@Override
	public String toString(boolean dynamic) {
		// TODO Auto-generated method stub
		return toString();
	}
}

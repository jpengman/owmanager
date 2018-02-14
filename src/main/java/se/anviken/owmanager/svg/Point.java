package se.anviken.owmanager.svg;

public class Point {
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(String pointString) {
		String[] xy = pointString.split(",");
		this.x = Integer.parseInt(xy[0]);
		this.y = Integer.parseInt(xy[1]);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	private int x;
	private int y;

}

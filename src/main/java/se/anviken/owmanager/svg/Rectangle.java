package se.anviken.owmanager.svg;

public class Rectangle extends BasicShape implements Shape {

	private int x, y, width, height;
	private Integer ry, rx;

	public int getHeight() {
		return height;
	}

	public int getRx() {
		return rx;
	}

	public Rectangle(int x, int y, Integer ry, Integer rx, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.ry = ry;
		this.rx = rx;
		this.width = width;
		this.height = height;
	}

	public int getRy() {
		return ry;
	}

	public Rectangle(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setRx(int rx) {
		this.rx = rx;
	}

	public void setRy(int ry) {
		this.ry = ry;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		String radiusValuesString = "";
		if (rx != null || ry != null) {
			if (ry == null || rx == ry) {
				radiusValuesString = " rx='" + rx + "'";
			} else if (rx == null) {
				radiusValuesString = " ry='" + ry + "'";
			} else {
				radiusValuesString = " rx='" + rx + "' ry='" + ry + "'";
			}
		}
		return "\n <rect x='" + x + "' y='" + y + "'" + radiusValuesString + " width='" + width + "' height='" + height
				+ "'" + getBasicShapeAttribute() + "/>";
	}

	@Override
	public String toString(boolean dynamic) {
		// TODO Auto-generated method stub
		return toString();
	}

}

package se.anviken.owmanager.svg;

public class Stop {
	private String offset;
	private String color = null; // stop-color
	private int opacity = 1; // stop-opacity

	@Override
	public String toString() {
		String offsetString = " offset='" + offset + "'";
		String stopColorString = "";
		if (color != null) {
			stopColorString = " stop-color='" + color + "'";
		}
		String stopOpacityString = "";
		if (opacity != 1) {
			stopColorString = " stop-opacity='" + opacity + "'";
		}
		return "<stop" + offsetString + stopColorString + stopOpacityString + "/>\n";
	}

	public Stop(String offset) {
		super();
		this.offset = offset;
	}

	public Stop(String offset, String color) {
		super();
		this.offset = offset;
		this.color = color;
	}

	public Stop(String offset, String color, int opacity) {
		super();
		this.offset = offset;
		this.color = color;
		this.opacity = opacity;
	}
}

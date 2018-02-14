package se.anviken.owmanager.svg;

public class Stop {
	private String offset;
	private String color = null; // stop-color
	private int opacity = 1; // stop-opacity

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(SVG.newline + SVG.indent + SVG.indent + "<stop offset='" + offset + "'");
		if (color != null) {
			sb.append(" stop-color='" + color + "'");
		}
		if (opacity != 1) {
			sb.append(" stop-opacity='" + opacity + "'");
		}
		sb.append("/>");
		return sb.toString();
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

	public Stop(int percent, String color) {
		super();
		this.offset = percent + "%";
		this.color = color;
	}
}

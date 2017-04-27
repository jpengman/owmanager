package se.anviken.owmanager.svg;

public class BasicShape {
	private String stroke = null;
	private String strokeWidth = null;
	private String fill = null;
	private Gradient gradient = null;

	public String getStroke() {
		return stroke;
	}

	public String getStrokeWidth() {
		return strokeWidth;
	}

	public String getStrokeString() {
		if (stroke != null) {
			return " stroke='" + stroke + "'";
		}
		return "";
	}

	public String getStrokeWidthString() {
		if (strokeWidth != null) {
			return " stroke-width='" + strokeWidth + "'";
		}
		return "";
	}

	public void setStroke(String stroke) {
		this.stroke = stroke;
	}

	public void setStrokeWidth(String strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public String getFill() {
		return fill;
	}

	public String getFillString() {
		if (fill != null) {
			return " fill='" + fill + "'";
		}
		if (gradient != null) {
			return " fill='url(#" + gradient.getId() + ")'";
		}
		return "";
	}

	public void setFill(String fill) {
		this.fill = fill;
		this.gradient = null;
	}

	public Gradient getGradient() {
		return gradient;
	}

	public void setGradient(Gradient gradient) {
		this.gradient = gradient;
		this.fill = null;
	}

	public String getBasicShapeAttribute() {
		return getFillString() + getStrokeString() + getStrokeWidthString();
	}
}

package se.anviken.owmanager.svg;

public class Text extends BasicShape implements Shape {

	private String text;
	private int x;
	private int y;
	private Integer fontSize;

	public String getTextAnchor() {
		return textAnchor;
	}

	public void setTextAnchor(String textAnchor) {
		this.textAnchor = textAnchor;
	}

	private String fontFamily;
	private String textAnchor;

	public Text(String text, int x, int y, int fontSize, String fontFamily) {
		super();
		setProperties(text, x, y, fontSize, fontFamily);
	}

	private void setProperties(String text, int x, int y, Integer fontSize, String fontFamily) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public Text(String text) {
		super();
		setProperties(text, 0, 10, null, "");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(SVG.newline + SVG.indent + "<text ");
		if (getX() != 0) {
			sb.append(" x='" + getX() + "'");
		}
		if (getY() != 0) {
			sb.append(" y='" + getY() + "'");
		}
		if (getFontSize() != null) {
			sb.append(" font-size='" + getFontSize() + "'");
		}
		if (getFontFamily() != null) {
			sb.append(" font-family='" + getFontFamily() + "'");
		}
		if (getTextAnchor() != null) {
			sb.append(" text-anchor='" + getTextAnchor() + "'");
		}
		sb.append(getBasicShapeAttribute() + ">" + getText() + "</text>");
		return sb.toString();
	}

	@Override
	public String toString(boolean dynamic) {
		// TODO Auto-generated method stub
		return toString();
	}
}

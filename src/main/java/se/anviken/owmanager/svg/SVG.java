package se.anviken.owmanager.svg;

import java.util.ArrayList;
import java.util.List;

public class SVG {
	private int width;
	private int height;
	private List<Definition> definitions = new ArrayList<Definition>();
	private List<Shape> shapes = new ArrayList<Shape>();
	private boolean dynamic = false;
	public static String newline = "\n";
	public static String indent = " ";

	public SVG(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	public void addLinearGradient(LinearGradient linearGradient) {
		definitions.add(linearGradient);
	}

	public String getDefinitionsString() {
		String defsTag = "";
		if (definitions != null) {
			for (Definition d : definitions) {
				defsTag += d.toString(dynamic);
			}
		}
		return defsTag;
	}

	public String getShapesString() {
		String shapesTags = "";
		if (shapes != null) {
			for (Shape s : shapes) {
				shapesTags += s.toString(dynamic);
			}
		}
		return shapesTags;
	}

	public void addShape(Shape shape) {
		shapes.add(shape);
	}

	@Override
	public String toString() {
		String startTag;
		if (dynamic) {
			startTag = "<svg width='100%' height='100%'>";
		} else {
			startTag = "<svg width='" + width + "' height='" + height + "'>";
		}
		String endtag = "</svg>";
		return startTag + newline + indent + "<defs>" + getDefinitionsString() + newline + indent + "</defs>"
				+ getShapesString() + newline + endtag;
	}
}

package de.hsb.webprog2.drawing.model.draw;

import java.awt.Color;

import org.codehaus.jackson.JsonNode;

public class DrawMessage {
	
	public static Color NONE = new Color(0, 0, 0, 0);
	
	private DrawType type;
	private boolean animate;
	private int lineColor;
	private int fillColor;
	private JsonNode content;
	
	public DrawMessage() {
	}

	public DrawType getType() {
		return type;
	}

	public void setType(DrawType type) {
		this.type = type;
	}

	public JsonNode getContent() {
		return content;
	}

	public void setContent(JsonNode content) {
		this.content = content;
	}

	public boolean isAnimate() {
		return animate;
	}

	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	public int getFillColor() {
		return fillColor;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}
}

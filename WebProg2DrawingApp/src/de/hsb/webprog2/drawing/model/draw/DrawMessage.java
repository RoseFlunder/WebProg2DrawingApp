package de.hsb.webprog2.drawing.model.draw;

import org.codehaus.jackson.JsonNode;

public class DrawMessage {
	
	private DrawType type;
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
}

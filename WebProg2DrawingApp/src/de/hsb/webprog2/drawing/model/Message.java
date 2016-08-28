package de.hsb.webprog2.drawing.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.JsonNode;

@XmlRootElement
public class Message {
	private String user;
	private MessageType type;
	
	private JsonNode content;
	
	public Message() {
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public JsonNode getContent() {
		return content;
	}

	public void setContent(JsonNode content) {
		this.content = content;
	}
}

package de.hsb.webprog2.drawing.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.JsonNode;

@XmlRootElement
public class Message {
	private String id;
	private String user;
	private MessageType type;
	private Date timestamp;
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}

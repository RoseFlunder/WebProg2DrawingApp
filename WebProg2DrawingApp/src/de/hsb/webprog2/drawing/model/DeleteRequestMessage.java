package de.hsb.webprog2.drawing.model;

public class DeleteRequestMessage {

	public enum DeleteMode {
		SINGLE, ALL_BEFORE, ALL_AFTER;
	}

	private String drawMessageId;
	private DeleteMode mode;

	public DeleteRequestMessage() {
	}

	public String getDrawMessageId() {
		return drawMessageId;
	}

	public void setDrawMessageId(String drawMessageId) {
		this.drawMessageId = drawMessageId;
	}

	public DeleteMode getMode() {
		return mode;
	}

	public void setMode(DeleteMode mode) {
		this.mode = mode;
	}

}
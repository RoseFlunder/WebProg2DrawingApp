package de.hsb.webprog2.drawing.model;

import java.util.Set;

public class DeleteMessage {
	
	private Set<String> messageIdsToDelete;

	public Set<String> getMessageIdsToDelete() {
		return messageIdsToDelete;
	}

	public void setMessageIdsToDelete(Set<String> messageIdsToDelete) {
		this.messageIdsToDelete = messageIdsToDelete;
	}

}
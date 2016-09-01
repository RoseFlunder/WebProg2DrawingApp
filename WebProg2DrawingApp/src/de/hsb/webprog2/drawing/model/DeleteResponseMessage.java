package de.hsb.webprog2.drawing.model;

import java.util.Set;

public class DeleteResponseMessage {
	
	private Set<String> deletedIds;
	
	public DeleteResponseMessage() {
	}

	public Set<String> getDeletedIds() {
		return deletedIds;
	}

	public void setDeletedIds(Set<String> deletedIds) {
		this.deletedIds = deletedIds;
	}

}

package de.hsb.webprog2.drawing.model;

import java.util.Set;

public class DeleteMessage {
	
	private Set<String> idsToDelete;
	
	public DeleteMessage() {
	}

	public Set<String> getIdsToDelete() {
		return idsToDelete;
	}

	public void setIdsToDelete(Set<String> idsToDelete) {
		this.idsToDelete = idsToDelete;
	}
}

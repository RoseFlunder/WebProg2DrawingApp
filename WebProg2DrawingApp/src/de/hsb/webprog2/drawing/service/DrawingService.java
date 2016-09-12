package de.hsb.webprog2.drawing.service;

import java.util.Deque;
import java.util.Set;

import de.hsb.webprog2.drawing.model.DeleteRequestMessage.DeleteMode;
import de.hsb.webprog2.drawing.model.Message;

public interface DrawingService {
	
	public void addDrawingMessageToHistory(Message msg);
	
	public void replaceDrawingMessageInHistory(Message msg);
	
	public Set<String> removeFromHistory(String id, DeleteMode mode);

	public Deque<Message> getHistory();
	
}

package de.hsb.webprog2.drawing.service;

import java.util.Deque;
import java.util.Set;

import de.hsb.webprog2.drawing.model.Message;

public interface DrawingService {
	
	public void addDrawingMessageToHistory(Message msg);
	
	public void removeDrawingMessagesFromHistory(Set<String> ids);

	public Deque<Message> getHistory();
	
}

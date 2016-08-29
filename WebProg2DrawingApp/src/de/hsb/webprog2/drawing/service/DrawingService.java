package de.hsb.webprog2.drawing.service;

import java.util.Deque;

import de.hsb.webprog2.drawing.model.Message;

public interface DrawingService {
	
	public void addDrawingMessageToHistory(Message msg);

	public Deque<Message> getHistory();
	
}

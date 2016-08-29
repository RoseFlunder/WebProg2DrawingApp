package de.hsb.webprog2.drawing.service;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import de.hsb.webprog2.drawing.model.Message;

public class DrawingServiceImpl implements DrawingService {

	private Deque<Message> history = new LinkedBlockingDeque<>();
	
	@Override
	public void addDrawingMessageToHistory(Message msg) {
		history.addLast(msg);
	}

	@Override
	public Deque<Message> getHistory() {
		return new LinkedList<>(history);
	}

}

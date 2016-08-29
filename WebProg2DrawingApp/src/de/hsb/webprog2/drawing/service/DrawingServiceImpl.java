package de.hsb.webprog2.drawing.service;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import de.hsb.webprog2.drawing.model.draw.DrawMessage;

public class DrawingServiceImpl implements DrawingService {

	private Deque<DrawMessage> history = new LinkedBlockingDeque<>();
	
	@Override
	public void addDrawingMessageToHistory(DrawMessage msg) {
		history.addLast(msg);
	}

	@Override
	public Deque<DrawMessage> getHistory() {
		return history;
	}

}

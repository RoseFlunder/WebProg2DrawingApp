package de.hsb.webprog2.drawing.service;

import java.util.Deque;

import de.hsb.webprog2.drawing.model.draw.DrawMessage;

public interface DrawingService {
	
	public void addDrawingMessageToHistory(DrawMessage msg);

	public Deque<DrawMessage> getHistory();
	
}

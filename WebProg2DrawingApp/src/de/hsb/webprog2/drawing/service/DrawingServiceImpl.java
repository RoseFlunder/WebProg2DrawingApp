package de.hsb.webprog2.drawing.service;

import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import de.hsb.webprog2.drawing.model.Message;

public class DrawingServiceImpl implements DrawingService {

	private Deque<Message> history = new LinkedBlockingDeque<>();
	
	@Override
	public void addDrawingMessageToHistory(Message msg) {
		if (msg.getId() == null)
			msg.setId(UUID.randomUUID().toString());
		if (msg.getTimestamp() == null)
			msg.setTimestamp(new Date());
		history.addLast(msg);
	}

	@Override
	public Deque<Message> getHistory() {
		return new LinkedList<>(history);
	}

	@Override
	public void removeDrawingMessagesFromHistory(Set<String> ids) {
		synchronized (history) {
			for (Iterator<Message> iterator = history.iterator(); iterator.hasNext();) {
				Message message = iterator.next();
				if (ids.contains(message.getId()))
					iterator.remove();
			}
		}
	}

}

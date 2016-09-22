package de.hsb.webprog2.drawing.service;

import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import de.hsb.webprog2.drawing.model.DeleteRequestMessage.DeleteMode;
import de.hsb.webprog2.drawing.model.Message;

public class DrawingServiceImpl implements DrawingService {

	private Deque<Message> history = new LinkedBlockingDeque<>();

	@Override
	public synchronized void addDrawingMessageToHistory(Message msg) {
		history.addLast(msg);
	}

	@Override
	public synchronized boolean replaceDrawingMessageInHistory(Message msg) {
		for (Iterator<Message> iterator = history.iterator(); iterator.hasNext();) {
			Message message = iterator.next();
			if (msg.getId().equals(message.getId())) {
				message.setContent(msg.getContent());
				return true;
			}
		}
		return false;
	}

	@Override
	public Deque<Message> getHistory() {
		return new LinkedList<>(history);
	}

	@Override
	public synchronized Set<String> removeFromHistory(String id, DeleteMode mode) {
		Set<String> deletedIds = new HashSet<>();

		switch (mode) {
		case SINGLE:
			if (history.removeIf(element -> id.equals(element.getId())))
				deletedIds.add(id);
			break;

		case ALL_BEFORE:
			for (Iterator<Message> iterator = history.iterator(); iterator.hasNext();) {
				Message message = iterator.next();
				if (message.getId().equals(id))
					break;
				deletedIds.add(message.getId());
				iterator.remove();
			}
			break;

		case ALL_AFTER:
			for (Iterator<Message> iterator = history.descendingIterator(); iterator.hasNext();) {
				Message message = iterator.next();
				if (message.getId().equals(id))
					break;
				deletedIds.add(message.getId());
				iterator.remove();
			}
			break;

		default:
			break;
		}

		return deletedIds;
	}

	@Override
	public synchronized void removeFromHistory(Set<String> idsToDelete) {
		history.removeIf(m -> idsToDelete.contains(m.getId()));
	}
}

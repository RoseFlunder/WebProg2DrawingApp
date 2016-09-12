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
	public void addDrawingMessageToHistory(Message msg) {
		history.addLast(msg);
	}
	
	@Override
	public void replaceDrawingMessageInHistory(Message msg) {
		synchronized (history) {
			for (Iterator<Message> iterator = history.iterator(); iterator.hasNext();) {
				Message message = iterator.next();
				if (msg.getId().equals(message.getId())){
					message.setContent(msg.getContent());
					break;
				}
			}
		}		
	}

	@Override
	public Deque<Message> getHistory() {
		return new LinkedList<>(history);
	}

	@Override
	public Set<String> removeFromHistory(String id, DeleteMode mode) {
		Set<String> deletedIds = new HashSet<>();
		
		synchronized (history) {
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
		}
		
		return deletedIds;
	}


}

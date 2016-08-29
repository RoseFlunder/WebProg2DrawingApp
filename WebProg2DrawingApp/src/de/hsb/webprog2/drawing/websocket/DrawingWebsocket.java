package de.hsb.webprog2.drawing.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import de.hsb.webprog2.drawing.model.Message;
import de.hsb.webprog2.drawing.service.DrawingService;

/**
 * Endpoint is added to the container in the context listener to provide the
 * drawing service CDI could be more elegant, but don't know if additional
 * librarys are allowed
 */
public class DrawingWebsocket {

	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
	private DrawingService drawingService;

	@OnOpen
	public void open(Session session, EndpointConfig config) {
		drawingService = (DrawingService) config.getUserProperties().get(DrawingService.class.getName());
		clients.add(session);

		for (Message message : drawingService.getHistory()) {
			try {
				session.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
		}
	}

	@OnClose
	public void close(Session session) {
		clients.remove(session);
	}

	@OnMessage
	public void onMessage(Session session, Message msg) {
		switch (msg.getType()) {
		case DRAWMESSAGE:
			drawingService.addDrawingMessageToHistory(msg);
			break;

		default:
			break;
		}

		synchronized (clients) {
			for (Iterator<Session> iterator = clients.iterator(); iterator.hasNext();) {
				Session client = iterator.next();
				try {
					if (client.isOpen())
						client.getBasicRemote().sendObject(msg);
				} catch (IOException | EncodeException e) {
					iterator.remove();
				}
			}
		}
	}

	@OnError
	public void error(Session session, Throwable t) {
		clients.remove(session);
	}
}
package de.hsb.webprog2.drawing.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.codehaus.jackson.map.ObjectMapper;

import de.hsb.webprog2.drawing.model.Message;
import de.hsb.webprog2.drawing.model.draw.DrawMessage;
import de.hsb.webprog2.drawing.service.DrawingService;

/**
 * Endpoint is added to the container in the context listener to provide the
 * drawing service CDI could be more elegant, but don't know if additional
 * librarys are allowed
 */
public class DrawingWebsocket {

	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
	private DrawingService drawingService;

	private ObjectMapper mapper = new ObjectMapper();

	@OnOpen
	public void open(Session session, EndpointConfig config) {
		drawingService = (DrawingService) config.getUserProperties().get(DrawingService.class.getName());
		clients.add(session);
	}

	@OnClose
	public void close(Session session) {
		clients.remove(session);
	}

	@OnMessage
	public void onMessage(Session session, Message msg) {
		switch (msg.getType()) {
		case DRAWMESSAGE:
			try {
				DrawMessage drawMessage = mapper.readValue(msg.getContent(), DrawMessage.class);
				drawingService.addDrawingMessageToHistory(drawMessage);
				break;
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		default:
			break;
		}

		synchronized (clients) {
			for (Session client : clients) {
				try {
					client.getBasicRemote().sendObject(msg);
				} catch (IOException | EncodeException e) {
					System.err.println("could not send message");
				}
			}
		}
	}
}
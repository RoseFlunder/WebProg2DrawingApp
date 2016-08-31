package de.hsb.webprog2.drawing.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

import org.codehaus.jackson.map.ObjectMapper;

import de.hsb.webprog2.drawing.model.DeleteMessage;
import de.hsb.webprog2.drawing.model.Message;
import de.hsb.webprog2.drawing.service.DrawingService;
import de.hsb.webprog2.drawing.websocket.robot.DrawRobot;

/**
 * Endpoint is added to the container in the context listener to provide the
 * drawing service CDI could be more elegant, but don't know if additional
 * librarys are allowed
 */
public class DrawingWebsocket {

	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
	
	private DrawingService drawingService;
	private ObjectMapper mapper = new ObjectMapper();
	private static DrawRobot robot;

	@OnOpen
	public void open(Session session, EndpointConfig config) {
		System.out.println("Open new session");
		drawingService = (DrawingService) config.getUserProperties().get(DrawingService.class.getName());
		synchronized (clients) {
			clients.add(session);
			checkRobot();
		}
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
		System.out.println("Session closed");
		synchronized (clients) {
			clients.remove(session);
			checkRobot();
		}
	}
	
	private void checkRobot(){
		if (clients.size() < 3 && (robot == null || !robot.isAlive())){
			try {
				robot = new DrawRobot(new URI("ws://localhost:8080/WebProg2DrawingApp/websocket/drawing"));
				robot.start();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (robot != null && robot.isAlive() && (clients.size() > 3 || clients.size() == 1)){
			robot.interrupt();
		}
	}

	@OnError
	public void error(Session session, Throwable t) {
		System.out.println("Error with session");
		clients.remove(session);
	}

	@OnMessage
	public void onMessage(Session session, Message msg) {
		switch (msg.getType()) {
		case DRAWMESSAGE:
			//only add new messages, if its a message that already has an ID its an animation message
			if (msg.getId() == null)
				drawingService.addDrawingMessageToHistory(msg);
			break;
		case DELETEMESSAGE:
			try {
				DeleteMessage deleteMsg = mapper.readValue(msg.getContent(), DeleteMessage.class);
				drawingService.removeDrawingMessagesFromHistory(deleteMsg.getMessageIdsToDelete());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

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
}
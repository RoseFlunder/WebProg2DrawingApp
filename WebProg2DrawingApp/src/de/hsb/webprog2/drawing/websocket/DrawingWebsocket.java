package de.hsb.webprog2.drawing.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;

import org.codehaus.jackson.map.ObjectMapper;

import de.hsb.webprog2.drawing.model.DeleteRequestMessage;
import de.hsb.webprog2.drawing.model.DeleteResponseMessage;
import de.hsb.webprog2.drawing.model.Message;
import de.hsb.webprog2.drawing.model.MessageType;
import de.hsb.webprog2.drawing.service.DrawingService;
import de.hsb.webprog2.drawing.websocket.robot.DrawRobot;

/**
 * Endpoint is added to the container in the context listener to provide the
 * drawing service CDI could be more elegant, but don't know if additional
 * librarys are allowed
 */
public class DrawingWebsocket {

	private static Map<String, Session> clients = Collections.synchronizedMap(new HashMap<>());

	private DrawingService drawingService;
	private ObjectMapper mapper = new ObjectMapper();
	private static DrawRobot robot;

	@OnOpen
	public void open(Session session, EndpointConfig config, @PathParam("username") String userName) {
		System.out.println("Open new session");
		drawingService = (DrawingService) config.getUserProperties().get(DrawingService.class.getName());
		
		if (clients.containsKey(userName)){
			int i = 1;
			while (clients.containsKey(userName + i)) {
				++i;
			}
			userName += i;
		}
		clients.put(userName, session);
		Message msg = new Message();
		msg.setType(MessageType.REGISTER_USERNAME_MESSAGE);
		msg.setUser(userName);
		try {
			session.getBasicRemote().sendObject(msg);
		} catch (IOException | EncodeException e1) {
			System.err.println(e1.getMessage());
		}
		
		Deque<Message> history = drawingService.getHistory();
		for (Message message : history) {
			System.out.println("sending history message");
			synchronized (session) {
				try {
					session.getBasicRemote().sendObject(message);
				} catch (IOException | EncodeException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		checkRobot();
	}

	@OnClose
	public void close(Session session, @PathParam("username") String userName) {
		System.out.println("Session closed");
		clients.remove(userName);
		checkRobot();
	}

	private static synchronized void checkRobot() {
		if (clients.size() < 3 && (robot == null || !robot.isAlive())) {
			try {
				robot = new DrawRobot(new URI("ws://localhost:8080/WebProg2DrawingApp/websocket/drawing/Robot"));
				robot.start();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (robot != null && robot.isAlive() && (clients.size() > 3 || clients.size() == 1)) {
			robot.stopRobot();
		}
	}

	@OnError
	public void error(Session session, Throwable t) {
		System.out.println("Error with session");
	}

	@OnMessage
	public void onMessage(Session session, Message msg) {
		msg.setTimestamp(new Date());

		switch (msg.getType()) {
		case DRAWMESSAGE:
			// only add new messages, if its a message that already has an ID
			// its an animation message
			if (msg.getId() == null) {
				msg.setId(UUID.randomUUID().toString());
				drawingService.addDrawingMessageToHistory(msg);
			}
			break;
		case DELETE_REQUEST_MESSAGE:
			try {
				DeleteRequestMessage deleteMsg = mapper.readValue(msg.getContent(), DeleteRequestMessage.class);
				Set<String> deletedIds = drawingService.removeFromHistory(deleteMsg.getDrawMessageId(), deleteMsg.getMode());
				
				DeleteResponseMessage response = new DeleteResponseMessage();
				response.setDeletedIds(deletedIds);
				msg.setType(MessageType.DELETE_RESPONSE_MESSAGE);
				msg.setContent(mapper.valueToTree(response));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			break;
			
			
		default:
			break;
		}

		synchronized (clients) {
			for (Iterator<Session> iterator = clients.values().iterator(); iterator.hasNext();) {
				Session client = iterator.next();
				synchronized (client) {
					if (client.isOpen()) {
						try {
							client.getBasicRemote().sendObject(msg);
						} catch (IOException | EncodeException e) {
							System.err.println("shit");
							System.err.println(e.getMessage());
						}
					}
				}
			}
		}
	}
}
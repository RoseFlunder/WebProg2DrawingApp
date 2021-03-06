package de.hsb.webprog2.drawing.websocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
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

import de.hsb.webprog2.drawing.model.ChatMessage;
import de.hsb.webprog2.drawing.model.DeleteMessage;
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

	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());

	private DrawingService drawingService;
	private ObjectMapper mapper = new ObjectMapper();
	private String robotUri;
	
	private static DrawRobot robot;

	@OnOpen
	public void open(Session session, EndpointConfig config, @PathParam("username") String userName) {
		drawingService = (DrawingService) config.getUserProperties().get(DrawingService.class.getName());
		robotUri = config.getUserProperties().get("websocket_url_robot").toString();
		clients.add(session);
		
		synchronized (session) {
			Message msg = new Message();
			msg.setType(MessageType.REGISTER_USERNAME_MESSAGE);
			msg.setUser(userName);
			try {
				session.getBasicRemote().sendObject(msg);
			} catch (IOException | EncodeException e) {
				System.err.println(e.getMessage());
			}
		}
		
		Deque<Message> history = drawingService.getHistory();
		for (Message message : history) {
			synchronized (session) {
				try {
					session.getBasicRemote().sendObject(message);
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			}
		}

		checkRobot(robotUri);
	}

	@OnClose
	public void close(Session session, @PathParam("username") String userName) {
		System.out.println("Session closed");
		clients.remove(session);
		checkRobot(robotUri);
	}

	private static synchronized void checkRobot(String robotURI) {
		if (clients.size() >=1 && clients.size() < 3 && robot == null) {
			try {
				robot = new DrawRobot(new URI(robotURI));
				robot.start();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (robot != null && (clients.size() > 3 || clients.size() <= 1)) {
			robot.stopRobot();
			robot = null;
		}
	}

	@OnError
	public void error(Session session, Throwable t) {
		try {
			Message msg = new Message();
			msg.setType(MessageType.CHATMESSAGE);
			msg.setUser("Server");
			
			StringWriter stringWriter = new StringWriter();
			
			try (PrintWriter writer = new PrintWriter(stringWriter)){
				t.printStackTrace(writer);
			} catch (Exception e) {
			}
			
			ChatMessage chatMsg = new ChatMessage();
			chatMsg.setMessage(t.getMessage() + "\n" + stringWriter.toString());
			msg.setContent(mapper.valueToTree(chatMsg));
			
			if (session.getBasicRemote() != null && session.isOpen()){
				session.getBasicRemote().sendObject(msg);
				session.close();
			}
				
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
		}
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
			} else {
				if (!drawingService.replaceDrawingMessageInHistory(msg))
					return;
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
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
			
		case DELETE_MESSAGE:
			try {
				DeleteMessage deleteMsg = mapper.readValue(msg.getContent(), DeleteMessage.class);
				drawingService.removeFromHistory(deleteMsg.getIdsToDelete());
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
				synchronized (client) {
					if (client.isOpen()) {
						try {
							client.getBasicRemote().sendObject(msg);
						} catch (IOException | EncodeException e) {
							System.err.println(e.getMessage());
						}
					}
				}
			}
		}
	}
}
package de.hsb.webprog2.drawing.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/drawing")
public class DrawingWebsocket {

	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());
	
	@OnOpen
	public void open(Session session, EndpointConfig config) {
		clients.add(session);
	}
	
	@OnClose
	public void close(Session session){
		clients.remove(session);
	}
	
	@OnMessage
	public void onMessage(Session session, String message){
		try {
			session.getBasicRemote().sendText("danke, beste");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

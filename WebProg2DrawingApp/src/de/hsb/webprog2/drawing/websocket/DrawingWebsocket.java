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
import javax.websocket.server.ServerEndpoint;

import de.hsb.webprog2.drawing.model.Message;
import de.hsb.webprog2.drawing.websocket.decoder.MessageDecoder;
import de.hsb.webprog2.drawing.websocket.encoder.MessageEncoder;

@ServerEndpoint(value = "/websocket/drawing", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
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
	public void onMessage(Session session, Message msg) {
		System.out.println(msg.getUser());
		System.out.println(msg.getType());
		synchronized (clients) {
			for (Session client : clients){
				try {
					client.getBasicRemote().sendObject(msg);
				} catch (IOException | EncodeException e) {
					System.err.println("could not send message");
				}
			}
		}
	}
	
}
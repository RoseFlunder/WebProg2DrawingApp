package de.hsb.webprog2.drawing.websocket.decoder;

import java.io.IOException;
import java.util.Set;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.map.ObjectMapper;

import de.hsb.webprog2.drawing.model.ChatMessage;
import de.hsb.webprog2.drawing.model.DeleteMessage;
import de.hsb.webprog2.drawing.model.Message;

public class MessageDecoder implements javax.websocket.Decoder.Text<Message>{
	
	private ObjectMapper mapper;

	@Override
	public void destroy() {

	}

	@Override
	public void init(EndpointConfig arg0) {
		mapper = new ObjectMapper();
	}

	@Override
	public Message decode(String s) throws DecodeException {
		try {
			Message msg = mapper.readValue(s, Message.class);
			return msg;
		} catch (IOException e) {
			throw new DecodeException(s, e.getMessage(), e);
		}
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}
	
	public static void main(String[] args) throws DecodeException{
		MessageDecoder decoder = new MessageDecoder();
		decoder.init(null);
		
		String json = "{\"user\": \"testuser\","
				+ " \"type\": \"DELETEMESSAGE\","
				+ " \"content\": {\"messageIdsToDelete\" : [\"hello world\", \"test\"]}}";
		Message msg = decoder.decode(json);
		System.out.println(msg.getType());
		System.out.println(msg.getUser());
		System.out.println(msg.getContent());
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			DeleteMessage chatMsg = mapper.readValue(msg.getContent(), DeleteMessage.class);
			Set<String> messageIdsToDelete = chatMsg.getMessageIdsToDelete();
			for (String string : messageIdsToDelete) {
				System.out.println(string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

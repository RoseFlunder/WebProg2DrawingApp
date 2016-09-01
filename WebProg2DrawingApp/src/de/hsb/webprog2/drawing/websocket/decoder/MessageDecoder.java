package de.hsb.webprog2.drawing.websocket.decoder;

import java.io.IOException;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.map.ObjectMapper;

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
}

package de.hsb.webprog2.drawing.websocket.encoder;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.map.ObjectMapper;

import de.hsb.webprog2.drawing.model.Message;

public class MessageEncoder implements javax.websocket.Encoder.Text<Message> {
	
	private ObjectMapper mapper;

	@Override
	public void destroy() {		
	}

	@Override
	public void init(EndpointConfig config) {
		mapper = new ObjectMapper();
	}

	@Override
	public String encode(Message msg) throws EncodeException {
		try {
			return mapper.writeValueAsString(msg);
		} catch (IOException e) {
			throw new EncodeException(msg, e.getMessage(), e);
		}
	}

}

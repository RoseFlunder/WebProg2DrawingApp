package de.hsb.webprog2.drawing.service;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import de.hsb.webprog2.drawing.websocket.DrawingWebsocket;
import de.hsb.webprog2.drawing.websocket.decoder.MessageDecoder;
import de.hsb.webprog2.drawing.websocket.encoder.MessageEncoder;

public class DrawingAppServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		DrawingServiceImpl drawingService = new DrawingServiceImpl();
		servletContext.setAttribute(DrawingService.class.getName(), drawingService);

		//deploy websocket
		try {			
			ServerContainer container = (ServerContainer) servletContext
					.getAttribute("javax.websocket.server.ServerContainer");
			ServerEndpointConfig c = ServerEndpointConfig.Builder.create(DrawingWebsocket.class, "/websocket/drawing/{username}")
					.decoders(Arrays.asList(MessageDecoder.class)).encoders(Arrays.asList(MessageEncoder.class))
					.build();
			c.getUserProperties().put(DrawingService.class.getName(), drawingService);
			c.getUserProperties().put("websocket_url_robot", servletContext.getInitParameter("websocket_url_robot"));
			container.addEndpoint(c);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

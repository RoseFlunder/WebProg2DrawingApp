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
		System.out.println("context listener started");

		ServletContext servletContext = sce.getServletContext();
		DrawingServiceImpl drawingService = new DrawingServiceImpl();
		servletContext.setAttribute(DrawingService.class.getName(), drawingService);

		//deploy websocket
		try {
			ServerContainer container = (ServerContainer) servletContext
					.getAttribute("javax.websocket.server.ServerContainer");
			ServerEndpointConfig c = ServerEndpointConfig.Builder.create(DrawingWebsocket.class, "/websocket/drawing")
					.decoders(Arrays.asList(MessageDecoder.class)).encoders(Arrays.asList(MessageEncoder.class))
					.build();
			c.getUserProperties().put(DrawingService.class.getName(), drawingService);
			container.addEndpoint(c);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

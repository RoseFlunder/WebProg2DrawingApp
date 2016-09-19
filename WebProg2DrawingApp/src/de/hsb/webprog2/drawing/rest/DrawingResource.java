package de.hsb.webprog2.drawing.rest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.spi.resource.Singleton;

import de.hsb.webprog2.drawing.model.Message;
import de.hsb.webprog2.drawing.model.draw.DrawCircleMessage;
import de.hsb.webprog2.drawing.model.draw.DrawLineMessage;
import de.hsb.webprog2.drawing.model.draw.DrawMessage;
import de.hsb.webprog2.drawing.model.draw.DrawPolygonMessage;
import de.hsb.webprog2.drawing.model.draw.DrawRectangleMessage;
import de.hsb.webprog2.drawing.service.DrawingService;

@Singleton
@Path("/drawing")
public class DrawingResource {

	@Context
	private ServletContext context;
	private DrawingService service;

	@PostConstruct
	private void init() {
		service = (DrawingService) context.getAttribute(DrawingService.class.getName());
	}

	@GET
	@Path("/history")
	@Produces(MediaType.APPLICATION_JSON)
	public Queue<Message> getValue() {
		return service.getHistory();
	}

	@GET
	@Path("/image")
	@Produces("image/png")
	public Response getImage() {
		BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) image.createGraphics();
		graphics.setBackground(Color.WHITE);
		graphics.clearRect(0, 0, 800, 600);
		ObjectMapper mapper = new ObjectMapper();

		for (Message message : service.getHistory()) {
			DrawMessage drawMsg;
			try {
				drawMsg = mapper.readValue(message.getContent(), DrawMessage.class);
				switch (drawMsg.getType()) {
				case LINE:
					graphics.setColor(new Color(drawMsg.getLineColor()));
					DrawLineMessage lineMsg = mapper.readValue(drawMsg.getContent(), DrawLineMessage.class);
					graphics.drawLine(lineMsg.getX1(), lineMsg.getY1(), lineMsg.getX2(), lineMsg.getY2());
					break;
				case CIRCLE:
					DrawCircleMessage circleMsg = mapper.readValue(drawMsg.getContent(), DrawCircleMessage.class);
					if (drawMsg.isUseFillColor()) {
						graphics.setColor(new Color(drawMsg.getFillColor()));
						graphics.fillOval(circleMsg.getX() - circleMsg.getRadius(),
								circleMsg.getY() - circleMsg.getRadius(), circleMsg.getRadius() * 2,
								circleMsg.getRadius() * 2);
					}
					
					graphics.setColor(new Color(drawMsg.getLineColor()));
					graphics.drawOval(circleMsg.getX() - circleMsg.getRadius(),
							circleMsg.getY() - circleMsg.getRadius(), circleMsg.getRadius() * 2,
							circleMsg.getRadius() * 2);
					
					break;
				case RECTANGLE:
					DrawRectangleMessage rectMsg = mapper.readValue(drawMsg.getContent(), DrawRectangleMessage.class);
					if (drawMsg.isUseFillColor()) {
						graphics.setColor(new Color(drawMsg.getFillColor()));
						graphics.fillRect(rectMsg.getX(), rectMsg.getY(), rectMsg.getWidth(), rectMsg.getHeight());
					}
					
					graphics.setColor(new Color(drawMsg.getLineColor()));
					graphics.drawRect(rectMsg.getX(), rectMsg.getY(), rectMsg.getWidth(), rectMsg.getHeight());
					break;
				case POLYGON:
					DrawPolygonMessage polyMsg = mapper.readValue(drawMsg.getContent(), DrawPolygonMessage.class);
					
					if (drawMsg.isUseFillColor()) {
						graphics.setColor(new Color(drawMsg.getFillColor()));
						graphics.fillPolygon(polyMsg.getxPoints(), polyMsg.getyPoints(), polyMsg.getxPoints().length);
					}
					
					graphics.setColor(new Color(drawMsg.getLineColor()));
					graphics.drawPolygon(polyMsg.getxPoints(), polyMsg.getyPoints(), polyMsg.getxPoints().length);
					break;
				default:
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			byte[] imageData = baos.toByteArray();
			return Response.ok(imageData).build();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Response.serverError().build();
	}
}

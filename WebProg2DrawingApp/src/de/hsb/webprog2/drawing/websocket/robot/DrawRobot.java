package de.hsb.webprog2.drawing.websocket.robot;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.hsb.webprog2.drawing.model.Message;
import de.hsb.webprog2.drawing.model.MessageType;
import de.hsb.webprog2.drawing.model.draw.DrawCircleMessage;
import de.hsb.webprog2.drawing.model.draw.DrawLineMessage;
import de.hsb.webprog2.drawing.model.draw.DrawMessage;
import de.hsb.webprog2.drawing.model.draw.DrawPolygonMessage;
import de.hsb.webprog2.drawing.model.draw.DrawRectangleMessage;
import de.hsb.webprog2.drawing.model.draw.DrawType;

@ClientEndpoint
public class DrawRobot extends Thread {
	
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	
	private Session session = null;
	private DrawType[] drawTypes = DrawType.values();
	private Random random = new Random();
	private ObjectMapper mapper = new ObjectMapper();
	
	public DrawRobot(URI endpointURI) {
		try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	@OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket for robot");
        session = userSession;
    }
	
	@OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket for robot");
        this.session = null;
    }
	
	@OnMessage
    public void onMessage(String message) {
        //do nothing
    }
	
	public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

	@Override
	public void run() {
		while (!isInterrupted()){
			System.out.println(this.getName() + " sending draw message");
			Message msg = new Message();
			msg.setUser("Robot");
			msg.setType(MessageType.DRAWMESSAGE);
			
			DrawMessage drawMsg = new DrawMessage();
			drawMsg.setType(drawTypes[random.nextInt(drawTypes.length)]);
			JsonNode drawContent = null;
			
			switch (drawMsg.getType()) {
			case LINE:
				DrawLineMessage lineMsg = new DrawLineMessage();
				lineMsg.setX1(random.nextInt(WIDTH));
				lineMsg.setX2(random.nextInt(WIDTH));
				lineMsg.setY1(random.nextInt(HEIGHT));
				lineMsg.setY2(random.nextInt(HEIGHT));
				drawContent = mapper.valueToTree(lineMsg);
				break;
				
			case CIRCLE:
				DrawCircleMessage circleMsg = new DrawCircleMessage();
				circleMsg.setX(random.nextInt(WIDTH));
				circleMsg.setY(random.nextInt(HEIGHT));
				circleMsg.setRadius(random.nextInt(WIDTH / 2));
				drawContent = mapper.valueToTree(circleMsg);
				break;
				
			case RECTANGLE:
				DrawRectangleMessage rectMsg = new DrawRectangleMessage();
				rectMsg.setX(random.nextInt(WIDTH));
				rectMsg.setY(random.nextInt(HEIGHT));
				rectMsg.setWidth(random.nextInt(WIDTH - rectMsg.getX()));
				rectMsg.setHeight(random.nextInt(HEIGHT - rectMsg.getY()));
				drawContent = mapper.valueToTree(rectMsg);
				break;
				
			case POLYGON:
				DrawPolygonMessage polyMsg = new DrawPolygonMessage();
				int points = random.nextInt(10);
				int[] xpoints = new int[points];
				int[] ypoints = new int[points];
				for (int i = 0; i < points; i++) {
					xpoints[i] = random.nextInt(WIDTH);
					ypoints[i] = random.nextInt(HEIGHT);
				}
				polyMsg.setxPoints(xpoints);
				polyMsg.setyPoints(ypoints);
				drawContent = mapper.valueToTree(polyMsg);
				break;

			default:
				break;
			}
			
			drawMsg.setContent(drawContent);
			msg.setContent(mapper.valueToTree(drawMsg));
			try {
				sendMessage(mapper.writeValueAsString(msg));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		try {
			this.session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
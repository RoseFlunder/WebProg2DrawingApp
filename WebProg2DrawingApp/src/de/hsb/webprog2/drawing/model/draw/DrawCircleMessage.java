package de.hsb.webprog2.drawing.model.draw;

public class DrawCircleMessage {
	
	private int x;
	private int y;
	
	private int radius;
	
	public DrawCircleMessage() {
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}
}

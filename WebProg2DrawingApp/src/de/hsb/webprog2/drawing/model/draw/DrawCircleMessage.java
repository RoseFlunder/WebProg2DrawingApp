package de.hsb.webprog2.drawing.model.draw;

public class DrawCircleMessage {
	
	private int x;
	private int y;
	
	private int radius;
	
	private int vx;
	private int vy;
	
	private int vRadius;
	
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

	public int getVx() {
		return vx;
	}

	public void setVx(int vx) {
		this.vx = vx;
	}

	public int getVy() {
		return vy;
	}

	public void setVy(int vy) {
		this.vy = vy;
	}

	public int getvRadius() {
		return vRadius;
	}

	public void setvRadius(int vRadius) {
		this.vRadius = vRadius;
	}
}

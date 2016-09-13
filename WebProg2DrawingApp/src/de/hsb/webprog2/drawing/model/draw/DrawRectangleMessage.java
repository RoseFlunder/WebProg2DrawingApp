package de.hsb.webprog2.drawing.model.draw;

public class DrawRectangleMessage {
	
	private int x;
	private int y;
	
	private int width;
	private int height;
	
	private int vx;
	private int vy;
	
	private int vWidth;
	private int vHeight;
	
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

	public int getvWidth() {
		return vWidth;
	}

	public void setvWidth(int vWidth) {
		this.vWidth = vWidth;
	}

	public int getvHeight() {
		return vHeight;
	}

	public void setvHeight(int vHeight) {
		this.vHeight = vHeight;
	}

	public DrawRectangleMessage() {
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}

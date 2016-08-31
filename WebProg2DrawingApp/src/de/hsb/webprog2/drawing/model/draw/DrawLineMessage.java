package de.hsb.webprog2.drawing.model.draw;

public class DrawLineMessage {
	
	private int x1;
	private int y1;
	
	private int x2;
	private int y2;
	
	private int vx1;
	private int vy1;
	
	private int vx2;
	private int vy2;
	
	public DrawLineMessage() {
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public int getVx1() {
		return vx1;
	}

	public void setVx1(int vx1) {
		this.vx1 = vx1;
	}

	public int getVy1() {
		return vy1;
	}

	public void setVy1(int vy1) {
		this.vy1 = vy1;
	}

	public int getVx2() {
		return vx2;
	}

	public void setVx2(int vx2) {
		this.vx2 = vx2;
	}

	public int getVy2() {
		return vy2;
	}

	public void setVy2(int vy2) {
		this.vy2 = vy2;
	}
}

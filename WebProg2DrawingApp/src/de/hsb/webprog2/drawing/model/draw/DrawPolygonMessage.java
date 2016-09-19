package de.hsb.webprog2.drawing.model.draw;

public class DrawPolygonMessage {
	
	private int[] xPoints;
	private int[] yPoints;
	
	private int vx[];
	private int vy[];
	
	public DrawPolygonMessage() {
	}

	public int[] getxPoints() {
		return xPoints;
	}

	public void setxPoints(int[] xPoints) {
		this.xPoints = xPoints;
	}

	public int[] getyPoints() {
		return yPoints;
	}

	public void setyPoints(int[] yPoints) {
		this.yPoints = yPoints;
	}

	public int[] getVx() {
		return vx;
	}

	public void setVx(int[] vx) {
		this.vx = vx;
	}

	public int[] getVy() {
		return vy;
	}

	public void setVy(int[] vy) {
		this.vy = vy;
	}
}

package de.hsb.webprog2.drawing.model.draw;

public class DrawPolygonMessage {
	
	private int[] xPoints;
	private int[] yPoints;
	
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

	
}

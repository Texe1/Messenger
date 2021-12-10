package gui.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public abstract class Button extends Text{
	protected boolean focussed = false, clicked = false;
	
//	public int x, y, width, height;
	public int edgeRadius = 10;
	
	protected String text = "";
	
	protected Color[] color = new Color[] {Color.WHITE, Color.LIGHT_GRAY, Color.GRAY};
	
	public Button(int x, int y, int width, int height, Color cNormal, Color cFocussed, Color cClicked) {
		this(x, y, width, height);
		
		color = new Color[] {cNormal, cFocussed, cClicked};
		this.bgColor = this.color[0];
	}
	
	public Button(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.bgColor = this.color[0];
		
		f = new Font(Font.MONOSPACED, Font.BOLD, Math.round(height)/2);
	}
	
	
	public Button(int x, int y, int width, int height) {
		super("", height/2, x, y);
		this.bgColor = this.color[0];
		absoluteCoords.width = width;
		absoluteCoords.height = height;
		
		f = new Font(Font.MONOSPACED, Font.BOLD, height/2);
	}
	
	public Button(int x, int y, int width, int height, Color color) {
		this(x, y, width, height);
		
		this.color = new Color[] {color, color.brighter(), color.darker()};
		this.bgColor = this.color[0];
	}
	
	public abstract void run();
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
	}

	public boolean isFocussed() {
		return focussed;
	}

	public void setFocussed(boolean focussed) {
		this.focussed = focussed;
		this.bgColor = color[focussed ? (clicked ? 2 : 1) : 0];
	}

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		if(this.clicked == true && clicked == false && focussed == true) {
			run();
		}
		this.clicked = clicked;
		
		this.bgColor = color[focussed ? (clicked ? 2 : 1) : 0];
		
	}
	
	public boolean isInBounds(int x, int y) {
		return (x > absoluteCoords.x && x < absoluteCoords.x + absoluteCoords.width && y > absoluteCoords.y && y < absoluteCoords.y + absoluteCoords.height);
	}
}

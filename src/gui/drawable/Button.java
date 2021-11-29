package gui.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public abstract class Button extends Drawable{
	protected boolean focussed = false, clicked = false;
	
	public int x, y, width, height;
	public int edgeRadius = 10;
	
	protected String text = "";
	
	private Font f;
	
	protected Color[] color = new Color[] {Color.WHITE, Color.LIGHT_GRAY, Color.GRAY};
	
	public Button(int x, int y, int width, int height, Color cNormal, Color cFocussed, Color cClicked) {
		this(x, y, width, height);
		
		color = new Color[] {cNormal, cFocussed, cClicked};
	}
	
	public Button(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		f = new Font(Font.MONOSPACED, Font.BOLD, height/2);
	}
	
	public Button(int x, int y, int width, int height, Color color) {
		this(x, y, width, height);
		
		this.color = new Color[] {color, color.brighter(), color.darker()};
	}
	
	public abstract void run();
	
	@Override
	public void draw(Graphics g) {
		g.setColor(color[focussed ? (clicked ? 2 : 1) : 0]);

		g.fillRoundRect(x, y, width, height, edgeRadius, edgeRadius);
		
		if(!text.isEmpty()) {
			g.setFont(f);
			int textWidth = g.getFontMetrics().stringWidth(text);
			g.setColor(Color.BLACK);
			g.drawString(text, x + (width - textWidth) / 2, y + (height + f.getSize()) / 2);
		}
	}

	public boolean isFocussed() {
		return focussed;
	}

	public void setFocussed(boolean focussed) {
		this.focussed = focussed;
	}

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		if(this.clicked == true && clicked == false && focussed == true) {
			run();
		}
		
		this.clicked = clicked;
	}
	
	public boolean isInBounds(int x, int y) {
		return (x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height);
	}
	
	public void setText(String s) {
		this.text = s;
	}
}

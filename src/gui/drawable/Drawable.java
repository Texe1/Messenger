package gui.drawable;

import java.awt.Graphics;

public abstract class Drawable {
	boolean dead;
	
	public boolean isDead() { return dead; }
	
	public abstract void draw(Graphics g);
}

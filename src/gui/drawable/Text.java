package gui.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class Text extends Drawable{
	private String s;
	private Font f;
	private Point pos;
	
	public int maxWidth = 0;
	public Color bgColor;
	
	public Text(String s, int fontSize, Point pos) {
		this.s = s;
		f = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
		this.pos = pos;
	}
	
	public Text(String s, int fontSize, int x, int y) {
		this.s = s;
		f = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
		this.pos = new Point(x, y);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setFont(f);
		if(maxWidth != 0) {
			int height;
			int y;
			String line = "";
			ArrayList<String> lines = new ArrayList<>();
			String[] words = s.split(" ");
			for (int i = 0; i < words.length; i++) {
				String testS = line + words[i];
				if(g.getFontMetrics().stringWidth(testS) > maxWidth) {
					lines.add(line);
					line = words[i];
				}else {
					line += " " + words[i];
				}
			}
			return;
		}
		g.setColor(Color.BLACK);
		g.setFont(f);
		g.drawString(s, pos.x, pos.y);
	}
}

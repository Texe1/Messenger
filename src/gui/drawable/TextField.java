package gui.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class TextField extends Button {

	public char[] permittedChars = new char[0];
	
	public String defaultText = "";
	
	private Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);
	
	private int maxChars = 0;
	
	public TextField(int x, int y, int width, int height) {
		super(x, y, width, height);
		maxChars = width/10;
	}

	public TextField(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, color);
		maxChars = width/20;
	}
	
	public TextField(int x, int y, int fontSize, int maxChars, String defaultText) {
		super(x, y, fontSize * maxChars /2 + fontSize*7/5, fontSize*2);
		changeFontsize(fontSize);
		this.maxChars = maxChars;
		this.defaultText = defaultText;
	}

	public boolean write = false;
	
	@Override
	public void run() {
		write = true;
	}
	
	public void unFocus() {
		write = false;
	}
	
	@Override
	public void draw(Graphics g) {

		g.setColor(color[focussed ? (clicked ? 2 : 1) : 0]);

		g.fillRoundRect(x, y, width, height, 10, 10);
		
		g.setFont(f);
		if(text.isEmpty()) {
			g.setColor(Color.GRAY);
			g.drawString(defaultText, x + (f.getSize()/2), y + (height+f.getSize())/2);
		}
		g.setColor(Color.BLACK);
		int textWidth = g.getFontMetrics(f).stringWidth(text);
		while(textWidth > width-f.getSize()) {
			text = text.substring(0, text.length()-1);
			textWidth = g.getFontMetrics(f).stringWidth(text);
		}
		g.drawString(text, x+(f.getSize()/2), y + (height+f.getSize())/2);
	}

	public void input(String s) {
		if (!write)
			return;
		
		for (int i = 0; i < s.length(); i++) {
			input(s.charAt(i));
		}
	}
	
	public void input(char c) {
		if (!write)
			return;
		if(c == 8) {
			if(text.length() > 0)
				text = text.substring(0, text.length() - 1);
		}else if(c == 127) {
			if (text.length() > 0)
				text = text.substring(0, text.length() - 1);
			while (text.length() > 0 && text.charAt(text.length() - 1) != ' ') {
				text = text.substring(0, text.length() - 1);
			}
		}else if(text.length() < maxChars){
			if(permittedChars.length > 0) {
				for (char d : permittedChars) {
					if(c == d) {
						text += "" + c;
						break;
					}
				}
			}else {
				text += "" + c;
			}
		}
	}
	
	public void changeFontsize(int i) {
		f = new Font(f.getName(), f.getStyle(), i);
	}
	
	public String getText() {
		return text;
	}
}

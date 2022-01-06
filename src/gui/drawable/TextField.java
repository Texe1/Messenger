package gui.drawable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class TextField extends Button {

	public char[] permittedChars = new char[0];

	public String defaultText = "";

	private int cursor = -1;
	private int selectionOrigin = 0; // where the user began pressing shift

	private Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);

	private int maxChars = 0;

	public TextField(int x, int y, int width, int height) {
		super(x, y, width, height);
		maxChars = width / 10;
		this.s = "TextField";
	}

	public TextField(int x, int y, int width, int height, Color color) {
		super(x, y, width, height, color);
		maxChars = width / 20;
		this.s = "TextField";
	}

	public TextField(int x, int y, int fontSize, int maxChars, String defaultText) {
		super(x, y, fontSize * maxChars / 2 + fontSize * 7 / 5, fontSize * 2);
		changeFontsize(fontSize);
		this.maxChars = maxChars;
		this.defaultText = defaultText;
		this.s = "TextField";
	}

	public TextField(float x, float y, float width, float height) {
		super(x, y, width, height);
		changeFontsize(20);
		this.s = "TextField";
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

		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(color[focussed ? (clicked ? 2 : 1) : write ? 1 : 0]);
		g2.fillRoundRect(absoluteCoords.x, absoluteCoords.y, absoluteCoords.width, absoluteCoords.height, 10, 10);

		if (write) {
			g2.setColor(color[2]);
			g2.setStroke(new BasicStroke(2));
			g2.drawRoundRect(absoluteCoords.x, absoluteCoords.y, absoluteCoords.width, absoluteCoords.height,
					edgeRadius, edgeRadius);
			g2.setStroke(new BasicStroke(1));
		}

		g2.setFont(f);
		if (text.isEmpty()) {
			g2.setColor(Color.GRAY);
			g2.drawString(defaultText, absoluteCoords.x + (f.getSize() / 2),
					absoluteCoords.y + (absoluteCoords.height + f.getSize()) / 2);
		}
		g2.setColor(Color.BLACK);
		int textWidth = g2.getFontMetrics(f).stringWidth(text);
		while (textWidth > absoluteCoords.width - f.getSize()) {
			if (text.length() == 0)
				break;
			textWidth = g2.getFontMetrics(f).stringWidth(text);
		}

		int X = absoluteCoords.x + (f.getSize() / 2);
		int Y = absoluteCoords.y + (absoluteCoords.height + f.getSize()) / 2;

		g2.setColor(fontColor);
		g2.drawString(text, X, Y);
		if (!text.isBlank() && cursor < text.length() && cursor >= 0) {
			if(cursor != selectionOrigin) {
				float[] hsb = Color.RGBtoHSB(70, 70, 255, null);
				g2.setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[1]));
				g2.drawString(text.substring(cursor < selectionOrigin ? cursor : selectionOrigin, (cursor > selectionOrigin ? cursor : selectionOrigin)+1), 
						X + g2.getFontMetrics().stringWidth(text.substring(0, cursor < selectionOrigin ? cursor : selectionOrigin)), Y);
			}
			g2.setColor(Color.BLUE);
			g2.drawString("" + text.charAt(cursor), 
					X + g2.getFontMetrics().stringWidth(text.substring(0, cursor)), Y);
		}
	}

	public void setPermittedChars(String s) {
		permittedChars = s.toCharArray();
	}

	public void addPermittedChar(char c) {
		permittedChars = (String.valueOf(permittedChars) + c).toCharArray();
	}

	public void input(String s) {
		if (!write)
			return;

		for (int i = 0; i < s.length(); i++) {
			input(s.charAt(i));
		}
	}

	public void input(char c) {

		if (cursor >= text.length())
			cursor = text.length() - 1;
		else if (cursor < 0)
			cursor = 0;

		if (text.length() == 0)
			cursor = -1;

		if (!write)
			return;

		if (c == 8) {
			if (text.length() > 0) {
				if(cursor > selectionOrigin) {
					int i = cursor;
					cursor = selectionOrigin;
					selectionOrigin = i;
				}
				text = text.substring(0, cursor) + (selectionOrigin < text.length() - 1 ? text.substring(selectionOrigin + 1) : "");
				
				selectionOrigin = --cursor;
			}
		} else if (c == 127) {
			if (text.length() > 0) {
				if(cursor > selectionOrigin) {
					int i = cursor;
					cursor = selectionOrigin;
					selectionOrigin = i;
				}
				if(cursor == selectionOrigin) {
					text = text.substring(0, cursor) + (cursor < text.length() - 1 ? text.substring(cursor + 1) : "");
				} else{
					text = text.substring(0, cursor) + (selectionOrigin < text.length() - 1 ? text.substring(selectionOrigin + 1) : "");
				}
				
				cursor--;
				
				while (cursor >= 0 && text.charAt(cursor) == ' ') {
					text = text.substring(0, cursor) + (cursor < text.length() - 1 ? text.substring(cursor + 1) : "");
					cursor--;
				}
				while (cursor >= 0 && text.charAt(cursor) != ' ') {
					text = text.substring(0, cursor) + (cursor < text.length() - 1 ? text.substring(cursor + 1) : "");
					cursor--;
				}
				
				selectionOrigin = cursor;
			}
		} else {
			if (text.length() < maxChars || maxChars == 0) {
				if (permittedChars.length > 0) {
					for (char d : permittedChars) {
						if (c == d) {
							text = text.substring(0, cursor + 1) + c
									+ (cursor < text.length() - 1 ? text.substring(cursor + 1) : "");
							selectionOrigin = ++cursor;
							break;
						}
					}
				} else {
					if (text.length() > 0)
						text = text.substring(0, cursor + 1) + c
								+ (cursor < text.length() - 1 ? text.substring(cursor + 1) : "");
					else
						text = "" + c;
					selectionOrigin = ++cursor;
				}
			}
		}
	}

	public void moveCusorForwards(boolean crtl, boolean shift) {
		if (!crtl) {
			if (cursor < text.length()-1) {
				cursor++;
			}
		}else {
			if (cursor < text.length()) {
					cursor++;
				while (cursor < text.length() && text.charAt(cursor) == ' ') {
					cursor++;
				}
				while (cursor < text.length() && text.charAt(cursor) != ' ') {
					cursor++;
				}
				cursor--;
			}
		}
		if (!shift) {
			selectionOrigin = cursor;
		}
	}

	public void moveCusorBack(boolean crtl, boolean shift) {
		if (!crtl) {
			if (cursor > 0) {
				cursor--;
			}
		} else {
			if (cursor > 0 || (cursor > 0 && text.length() == 0)) {
				if (cursor >= 0) {
					cursor--;
				}
				while (cursor >= 0 && text.charAt(cursor) == ' ') {
					cursor--;
				}
				while (cursor >= 0 && text.charAt(cursor) != ' ') {
					cursor--;
				}
				cursor++;
			}
		}
		if (!shift) {
			selectionOrigin = cursor;
		}
	}

	public void changeFontsize(int i) {
		f = new Font(f.getName(), f.getStyle(), i);
	}

	public String getText() {
		return text;
	}
}

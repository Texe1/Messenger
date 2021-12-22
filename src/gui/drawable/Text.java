package gui.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Text extends Drawable {
	protected Font f = new Font(Font.MONOSPACED, Font.PLAIN, 20);
	protected Color fontColor = Color.BLACK;

	public Color bgColor = null;

	public boolean isCentered = false;

	public Text(String s, int fontSize, int x, int y) {
		super(x, y, 0, 0);
		this.s = s;
		f = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
	}

	public Text(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public Text(String s, float x, float y, float width, float height) {
		super(x, y, width, height);
		this.s = s;
	}

	@Override
	public void draw(Graphics g) {
		g.setFont(f);
		if (absoluteCoords.width != 0) {
			String line = "";
			FontMetrics fm = g.getFontMetrics();
			ArrayList<String> lines = new ArrayList<>();
			String[] words = s.split(" ");

			for (int i = 0; i < words.length; i++) {
				String testS = line + words[i];
				if (fm.stringWidth(testS) > absoluteCoords.width) {
					lines.add(line);
					line = words[i];
					while (fm.stringWidth(line) > absoluteCoords.width) {
						lines.add(line.substring(0, Math.floorDiv(absoluteCoords.width, 12)));
						line = line.substring(Math.floorDiv(absoluteCoords.width, 12));

						if (line.isBlank() && ++i < words.length)
							line = words[i];
					}
				} else {
					line += " " + words[i];
				}
			}

			lines.add(line);
			absoluteCoords = new Rectangle(absoluteCoords.x, absoluteCoords.y, absoluteCoords.width,
					lines.size() * 30 + 10);
			

			if (draw) {
				int y = absoluteCoords.y + f.getSize() * 3 / 2;
				if (bgColor != null) {
					g.setColor(bgColor);
					g.fillRoundRect(absoluteCoords.x, absoluteCoords.y, absoluteCoords.width, absoluteCoords.height, 10,
							10);
				}
				g.setColor(fontColor);
				for (String s : lines) {
					int sWidth = g.getFontMetrics().stringWidth(s);
					g.drawString(s, absoluteCoords.x + (isCentered ? ((absoluteCoords.width - sWidth-10)/2) : 0), y);
					y += f.getSize() * 3 / 2;
				}
			}

			return;
		}
		if (!draw)
			return;
		g.setColor(fontColor);
		g.setFont(f);
		g.drawString(s, absoluteCoords.x+10, absoluteCoords.y + f.getSize() * 3 / 2);
	}

	public void setText(String s) {
		this.s = s;
	}

	public void setFontColor(Color c) {
		fontColor = c;
	}
}

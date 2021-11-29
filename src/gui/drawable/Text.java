package gui.drawable;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Text extends Drawable {
	private String s;
	private Font f;
	public Rectangle r = new Rectangle();

	public int maxWidth = 0;
	public Color bgColor;

	public Text(String s, int fontSize, Point pos) {
		this.s = s;
		f = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
	}

	public Text(String s, int fontSize, int x, int y) {
		this.s = s;
		f = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
		r.x = x;
		r.y = y;
	}

	@Override
	public void draw(Graphics g) {
		g.setFont(f);
		if (maxWidth != 0) {
			String line = "";
			FontMetrics fm = g.getFontMetrics();
			ArrayList<String> lines = new ArrayList<>();
			String[] words = s.split(" ");

			for (int i = 0; i < words.length; i++) {
				String testS = line + words[i];
				if (fm.stringWidth(testS) > maxWidth) {
					lines.add(line);
					line = words[i];
					while (fm.stringWidth(line) > maxWidth) {
						lines.add(line.substring(0, Math.floorDiv(maxWidth, 12)));
						line = line.substring(Math.floorDiv(maxWidth, 12));

						if (line.isBlank() && ++i < words.length)
							line = words[i];
					}
				} else {
					line += " " + words[i];
				}
			}

			lines.add(line);

			r = new Rectangle(r.x, r.y, maxWidth, lines.size()*30 +10);
			
			if (draw) {
				int y = r.y;
				g.setColor(bgColor);
				g.fillRoundRect(r.x, r.y-30, r.width, r.height, 10, 10);
				g.setColor(Color.BLACK);
				for (String s : lines) {
					g.drawString(s, r.x, y);
					y += 30;
				}
			}

			return;
		}
		if(!draw) return;
		g.setColor(Color.BLACK);
		g.setFont(f);
		g.drawString(s, r.x, r.y);
	}
}

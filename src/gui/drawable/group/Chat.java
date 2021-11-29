package gui.drawable.group;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Text;
import network.Client;

public class Chat extends Group {

	Client client;
	String s;

	Rectangle r;

	public CopyOnWriteArrayList<Text> messages = new CopyOnWriteArrayList<>();

	public Chat(Client client, int x, int y, int width, int height) {
		this.client = client;
		r = new Rectangle(x, y, width, height);
	}

	public void update(String name) {
		String[][] chats = client.getChats();
		String[] chat = null;

		for (int i = 0; i < chats.length; i++) {
			if (chats[i][0].equals(name)) {
				chat = chats[i];
				break;
			}
		}

		if (chat != null) {
			int x;
			String s;
			String org;
			String msg;
			for (int i = chat.length - 1; i >= 2; i--) {
				s = chat[i];

				if (s.startsWith("\\")) {
					msg = s.substring(1);
					Text t = new Text(msg, i, null);
					t.bgColor = Color.WHITE;
					t.draw = false;
					add(t);

					x = r.x;
					System.out.println(x);

				} else {
					msg = s.substring(s.indexOf('\\') + 1);

					x = r.x + r.width / 5;
					System.out.println(x);
				}

				Text text = new Text(msg, 20, x, 0);
				System.out.println(text.r.x);
				text.maxWidth = r.width * 4 / 5;
				text.bgColor = Color.LIGHT_GRAY;
				messages.add(text);
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		int y = r.height - 60;

		for (Text t : messages) {
			t.draw = false;
			t.draw(g);
			if (t.r.height > 0) {
				y -= t.r.height + 20;
				t.r.y = y;
				t.draw = true;
			}
//			t.draw(g);
		}
		
		super.draw(g);

	}

}

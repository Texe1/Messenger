package gui.drawable;

import java.awt.Rectangle;

import network.Client;

public class Chat extends Group {

	Client client;
	String s;

	Rectangle r;

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
			}
		}
		if (chat != null) {
			int y = r.height;
			String s;
			String org;
			String msg;
			for (int i = chat.length; i >= 2; i++) {
				s = chat[i];

				if (s.startsWith("\\")) {
					msg = s.substring(1);
					add(new Text(msg, i, null));
				} else {
					msg = s.substring(s.indexOf('\\') + 1);
					org = s.substring(0, s.indexOf('\\'));
				}

			}
		}
	}

}

package gui.drawable.group;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Button;
import gui.drawable.Text;
import gui.drawable.TextField;
import network.Client;

public class Chat extends Group {

	Client client;

	Rectangle r;

	public CopyOnWriteArrayList<Text> messages = new CopyOnWriteArrayList<>();

	private TextField textField;
	
	public Chat(Client client, int x, int y, int width, int height, String s) {
		this.client = client;
		this.name = s;
		r = new Rectangle(x, y, width, height);
		
		textField = new TextField(x, y + height -40, r.width - 110, 40);
		textField.defaultText = "message";
		add(textField);
		
		Button sendButton = new Button(r.x + width - 100, r.y + r.height -40, 100, 40) {
			
			@Override
			public void run() {
				client.sendThroughChat(name, textField.getText());
				textField.setText("");
			}
		};
		sendButton.setText("send");
		add(sendButton);
	}


	@Override
	public void update() {
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
			String s2;
			String msg;
			
			this.messages = new CopyOnWriteArrayList<>();
			
			for (int i = chat.length - 1; i >= 2; i--) {
				s2 = chat[i];

				if (s2.startsWith("\\")) {
					msg = s2.substring(1);
					Text t = new Text(msg, i, null);
					t.bgColor = Color.WHITE;
					t.draw = false;
					add(t);

					x = r.x;

				} else {
					msg = s2.substring(s2.indexOf('\\') + 1);

					x = r.x + r.width / 5;
				}

				Text text = new Text(msg, 20, x, 0);
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
			t.draw(g);
		}
		
		super.draw(g);

	}

}

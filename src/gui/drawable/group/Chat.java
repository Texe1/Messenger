package gui.drawable.group;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Button;
import gui.drawable.Text;
import gui.drawable.TextField;
import gui.general.Frame;
import network.Client;

public class Chat extends Group {

	Client client;
	
	public CopyOnWriteArrayList<Text> messages = new CopyOnWriteArrayList<>();

	private TextField textField;
	
	public Chat(Client client, String s) {
		super();
		this.restrictUpdate = false;
		this.client = client;
		this.name = s;
		this.s = "Chat<" + s + ">";
		
		textField = new TextField(0f, 40f, 210f, 0f);
		textField.setCoordType(1, CoordType.DIST);
		textField.setCoordType(2, CoordType.DIST);
		textField.setCoordType(3, CoordType.DIST);
		textField.defaultText = "message";
		add(textField);
		
		Button sendButton = new Button(200f, 40f, 160f, 40f) {
			
			@Override
			public void run() {
				client.sendThroughChat(name, textField.getText());
				textField.setText("");
			}
		};
		sendButton.setCoordType(0, CoordType.DIST);
		sendButton.setCoordType(1, CoordType.DIST);
		sendButton.setText("send");
		add(sendButton);
	}


	@Override
	public void update(Frame f, Rectangle r, boolean forceUpdate) {
		super.update(f, r, forceUpdate);
		String[] chat = client.getChat(name);

		boolean updated = false;
		
		if (chat != null) {
			
			int x;
			String s2;
			String msg;
			
//			this.messages = new CopyOnWriteArrayList<>();
			
			for (int i = this.messages.size()+2; i < chat.length; i++) {
				s2 = chat[i];

				updated = true;
				
				if (s2.startsWith("\\")) {
					msg = s2.substring(1);
					Text t = new Text(msg, i, 0, 0);
					t.bgColor = Color.WHITE;
					t.draw = false;
					
					x = absoluteCoords.x;

				} else {
					msg = s2.substring(s2.indexOf('\\') + 1);

					x = absoluteCoords.x + absoluteCoords.width / 5;
				}

				Text text = new Text(msg, 20, x, 0);
				text.absoluteCoords.width = absoluteCoords.width * 4 / 5;
				text.bgColor = Color.LIGHT_GRAY;
				text.draw = false;
				messages.add(text);
			}
			
			if(updated || forceUpdate) {
				for (Text t : messages) {
					t.draw = false;
				}
			}
		}
	}

	@Override
	public void draw(Graphics g) {
		int y = absoluteCoords.height - 60;

		for (int i = messages.size()-1; i >= 0; i--) {
			Text t = messages.get(i);
			if(!t.draw)t.draw(g);
			if (t.absoluteCoords.height > 0) {
				y -= t.absoluteCoords.height + 20;
				if(!t.draw) t.absoluteCoords.y = y;
				t.draw = true;
			}
			t.draw(g);
		}
		
		super.draw(g);

	}

}

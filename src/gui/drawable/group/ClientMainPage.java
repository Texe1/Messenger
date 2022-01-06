package gui.drawable.group;

import java.awt.Color;

import gui.drawable.Button;
import gui.drawable.Text;
import gui.drawable.TextField;
import gui.general.ClientFrame;

public class ClientMainPage extends Group{
	
	public TextField tf_hostIP, tf_port, tf_name;
	
	ClientFrame frame;
	
	public ClientMainPage(ClientFrame f) {
		super();
		this.setCoordType(2, CoordType.REL);
		this.setCoordType(3, CoordType.REL);
//		this.absoluteCoords = new Rectangle(100, 0, 0, 0);
		this.frame = f;
		
		this.s = "ClientMainPage";
		
		tf_hostIP = new TextField(.0f, 100.0f, 100.0f, 40.0f);
		tf_hostIP.setCoordType(2, CoordType.DIST);
		tf_hostIP.defaultText = "host IP";
		
		add(tf_hostIP);

		Text t = new Text(":", 100.0f, 100.0f, .0f, .0f);
		t.setCoordType(0, CoordType.DIST);
		t.setFontColor(Color.LIGHT_GRAY);
		t.bgColor = null;
		
		add(t);
		
		tf_port = new TextField(70f, 100f, 0f, 40f);
		tf_port.defaultText = "port";
		tf_port.setCoordType(0, CoordType.DIST);
		tf_port.setCoordType(2, CoordType.DIST);
		tf_port.setPermittedChars("0123456789");
		
		add(tf_port);

		tf_name = new TextField(.0f, 150f, 0f, 40f);
		tf_name.setCoordType(2, CoordType.DIST);
		tf_name.defaultText = "name";
		add(tf_name);

		Button b = new Button(70f, 200f, 140f, 40f) {

			@Override
			public void run() {
				if (frame.client.connected) {
					frame.client.disconnect();
					this.setText("connect");
				} else {
					if (tf_hostIP.getText().isBlank() || tf_port.getText().isBlank() || tf_name.getText().isBlank())
						return;
					frame.client.connect(tf_hostIP.getText(), Integer.parseInt(tf_port.getText()), tf_name.getText());
					if (frame.client.connected)
						this.setText("disconnect");
				}
			}
		};

		b.setCoordType(0, CoordType.DISTMID);
		b.setText("connect");

		add(b);

		this.name = "connection";
	}
	
}

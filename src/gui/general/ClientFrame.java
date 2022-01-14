package gui.general;

import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import gui.drawable.group.Chat;
import gui.drawable.group.ClientMainPage;
import gui.drawable.group.Menu;
import network.Client;

public class ClientFrame extends Frame {

	private static final long serialVersionUID = -8246131571704187284L;

	public Client client;
	
	public ClientFrame(Client client) {
		super(1200, 1000);
		drawingRect = new Rectangle(200, 0, 1000, 1000);
		
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				if(client.connected)client.disconnect();
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}
			
		});
		
		addFixed(new Menu(this));
		
		add(new ClientMainPage(this));
		
		showGroups(new int[] {0});
		
		update(true);

	}
	
	public void update(Client c, boolean forceUpdate) {
		this.client = c;
		update(forceUpdate);
	}

	@Override
	public void update(boolean forceUpdate) {
		super.update(forceUpdate);

		if (client == null || !client.receivedContacts())
			return;
		
		keepGroups(new int[] {0});


		String[] contacts = client.getContacts();

		for (String name : contacts) {
			add(new Chat(client, name));
		}
	}

}

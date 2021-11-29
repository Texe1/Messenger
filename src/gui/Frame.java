package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;

import gui.drawable.Button;
import gui.drawable.Text;
import gui.drawable.TextField;
import gui.drawable.group.Chat;
import gui.drawable.group.Group;
import gui.drawable.group.Menu;
import network.Client;

public class Frame extends JFrame {

	private static final long serialVersionUID = -8246131571704187284L;

	private CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();
	private Group currentGroup;

	private TextField tf_hostIP;
	private TextField tf_port;
	private TextField tf_name;
	

	private Menu menu;
	
	private Client client;

	private int drawCycle = 0; // updates at 100 cycles of drawing

	public Frame(Client client) {
		setSize(1200, 1000);
		setLocationRelativeTo(null);

		setBackground(Color.DARK_GRAY.darker());

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (client.connected)
					client.disconnect();
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				for (Button b : currentGroup.getButtons()) {
					b.setClicked(false);
				}

				for (Button b : menu.getButtons()) {
					b.setClicked(false);
				}

				for (TextField t : currentGroup.getTextFields()) {
					if (!t.isInBounds(e.getX(), e.getY()))
						t.unFocus();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				for (Button b : currentGroup.getButtons()) {
					if (b.isInBounds(e.getX(), e.getY())) {
						b.setClicked(true);
					}
				}
				for (Button b : menu.getButtons()) {
					if (b.isInBounds(e.getX(), e.getY())) {
						b.setClicked(true);
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				for (Button b : currentGroup.getButtons()) {
					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
				}

				for (Button b : menu.getButtons()) {
					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				for (Button b : currentGroup.getButtons()) {
					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
				}

				for (Button b : menu.getButtons()) {
					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
				}
			}
		});
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\t') {
					for (int i = 0; i < currentGroup.getTextFields().size(); i++) {
						if (currentGroup.getTextFields().get(i).write) {
							currentGroup.getTextFields().get(i).write = false;
							currentGroup.getTextFields()
									.get((i + 1) % currentGroup.getTextFields().size()).write = true;
							break;
						}
					}
				} else {
					for (TextField t : currentGroup.getTextFields()) {
						t.input("" + e.getKeyChar());
					}
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());

		setVisible(true);
		
		this.client = client;

		menu = new Menu(0, 100, 200, 1000, this);

		Group mainPage = new Group();

		tf_hostIP = new TextField(300, 100, 700, 40);
		tf_hostIP.defaultText = "host IP";
		mainPage.add(tf_hostIP);

		mainPage.add(new Text(":", 20, 1010, 125));

		tf_port = new TextField(1032, 100, 20, 4, "port");
		tf_port.permittedChars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		mainPage.add(tf_port);

		tf_name = new TextField(300, 150, 800, 40);
		tf_name.defaultText = "name";
		mainPage.add(tf_name);

		Button b = new Button(650, 200, 100, 40) {

			@Override
			public void run() {
				if (client.connected) {
					client.disconnect();
					this.setText("disconnect");
				} else {
					if (tf_hostIP.getText().isBlank() || tf_port.getText().isBlank() || tf_name.getText().isBlank())
						return;

					System.out.println(tf_hostIP.getText());
					client.connect(tf_hostIP.getText(), Integer.parseInt(tf_port.getText()), tf_name.getText());
					if (client.connected)
						this.setText("connect");
				}
			}
		};
		b.setText("connect");

		mainPage.add(b);

		mainPage.name = "connection";

		groups.add(mainPage);
		showGroup(0);

//		c.beginChat("Hello");
//		c.addToChat("Hello", "\\Test, test, test!");
//		c.addToChat("Hello", "Hello\\Test, test, test!");
//
//		Chat chat = new Chat(c, 250, 100, 750, 800, "Hello");
//		add(chat);
//
//		c.beginChat("Bye");
//		c.addToChat("Bye", "\\Bye, test, test!");
//		c.addToChat("Bye", "Bye\\Bye, test, test!");
//
//		Chat chat2 = new Chat(c, 250, 100, 750, 800, "Bye");
//		add(chat2);

		menu.update();

		createBufferStrategy(3);

		Thread t = new Thread() {
			@Override
			public void run() {
				while (this.isAlive()) {
					draw();
				}
			}
		};

		t.start();

	}

	public void showGroup(int i) {
		currentGroup = groups.get(i);
	}

	public CopyOnWriteArrayList<Group> getGroups() {
		return groups;
	}

	public void add(Group g) {
		groups.add(g);
		menu.update();
	}

	public void update() {
		
		for (Group group : groups) {
			group.update();
		}
		
		if(!client.receivedContacts) return;
		
		client.receivedContacts = false;
		
		Group mainPage = getGroups().get(0);
		
		groups = new CopyOnWriteArrayList<>();
		
		groups.add(mainPage);
		
		String[] contacts = client.getContacts();
		
		for (String name : contacts) {
			add(new Chat(client, 250, 100, 750, 600, name));
		}
		System.out.println("Why?");
		menu.update();
	}

	public void draw() {
		BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		g.clearRect(0, 0, getWidth(), getHeight());

		currentGroup.draw(g);

		menu.draw(g);

		drawCycle++;
		drawCycle %= 100;
		update();
		if (drawCycle == 0) {
			
		}

		g.dispose();

		bs.show();
	}

}

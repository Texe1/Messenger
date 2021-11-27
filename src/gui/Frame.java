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
import gui.drawable.Drawable;
import gui.drawable.Group;
import gui.drawable.Text;
import gui.drawable.TextField;
import network.Client;

public class Frame extends JFrame {

	private static final long serialVersionUID = -8246131571704187284L;

	private CopyOnWriteArrayList<Drawable> drawables = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<Button> buttons = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<TextField> textFields = new CopyOnWriteArrayList<>();

	private ArrayList<Group> groups = new ArrayList<>();
	
	private TextField tf_hostIP;
	private TextField tf_port;
	private TextField tf_name;

	public Frame(Client c) {
		setSize(1000, 1000);
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
				if (c.connected)
					c.disconnect();
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
				for (Button b : buttons) {
					b.setClicked(false);
				}

				for (TextField t : textFields) {
					if (!t.isInBounds(e.getX(), e.getY()))
						t.unFocus();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				for (Button b : buttons) {
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
				for (Button b : buttons) {
					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				for (Button b : buttons) {
					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
				}
			}
		});
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\t') {
					for (int i = 0; i < textFields.size(); i++) {
						if (textFields.get(i).write) {
							textFields.get(i).write = false;
							textFields.get((i + 1) % textFields.size()).write = true;
							break;
						}
					}
				} else {
					for (TextField t : textFields) {
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

		Group mainPage = new Group();
		
		tf_hostIP = new TextField(100, 100, 700, 40);
		tf_hostIP.defaultText = "host IP";
		mainPage.add(tf_hostIP);

		mainPage.add(new Text(":", 20, 810, 125));
		
		tf_port = new TextField(832, 100, 20, 4, "port");
		tf_port.permittedChars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		mainPage.add(tf_port);

		tf_name = new TextField(100, 150, 800, 40);
		tf_name.defaultText = "name";
		mainPage.add(tf_name);

		Button b = new Button(450, 200, 100, 40) {

			@Override
			public void run() {
				if (c.connected) {
					c.disconnect();
					this.setText("disconnect");
				} else {
					if (tf_hostIP.getText().isBlank() || tf_port.getText().isBlank() || tf_name.getText().isBlank())
						return;

					c.connect(tf_hostIP.getText(), Integer.parseInt(tf_port.getText()), tf_name.getText());
					if (c.connected)
						this.setText("connect");
				}
			}
		};
		b.setText("connect");

		mainPage.add(b);
		
		groups.add(mainPage);
		showGroup(0);
		
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
		this.drawables 	= groups.get(i).getDrawables();
		this.buttons 	= groups.get(i).getButtons();
		this.textFields	= groups.get(i).getTextFields();
	}

	public void draw() {
		BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		g.clearRect(0, 0, getWidth(), getHeight());

		for (Drawable d : drawables) {
			d.draw(g);
		}

		g.dispose();

		bs.show();
	}

	public void add(Drawable d) {
		drawables.add(d);
	}

	public void add(Button b) {
		buttons.add(b);
		drawables.add(b);
	}

	public void add(TextField t) {
		buttons.add(t);
		drawables.add(t);
		textFields.add(t);
	}

}

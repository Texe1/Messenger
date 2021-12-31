package gui.general;

import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import gui.drawable.group.Chat;
import gui.drawable.group.MainPage;
import gui.drawable.group.Menu;
import network.Client;

public class ClientFrame extends Frame {

	private static final long serialVersionUID = -8246131571704187284L;

//	private CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();
//	private Group currentGroup;

	private Menu menu;
	private MainPage mainPage;

	public Client client;

//	private int drawCycle = 0; // updates at 100 cycles of drawing

	public ClientFrame(Client client) {
		super(1200, 1000);
		drawingRect = new Rectangle(200, 0, 1000, 1000);
//
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
//
//			@Override
//			public void windowOpened(WindowEvent e) {
//			}
//
//			@Override
//			public void windowIconified(WindowEvent e) {
//			}
//
//			@Override
//			public void windowDeiconified(WindowEvent e) {
//			}
//
//			@Override
//			public void windowDeactivated(WindowEvent e) {
//			}
//
//			@Override
//			public void windowClosing(WindowEvent e) {
//				if (client.connected)
//					client.disconnect();
//				System.exit(0);
//			}
//
//			@Override
//			public void windowClosed(WindowEvent e) {
//			}
//
//			@Override
//			public void windowActivated(WindowEvent e) {
//			}
//		});
//		addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				for (Button b : currentGroup.getButtons()) {
//					b.setClicked(false);
//				}
//
//				for (Button b : menu.getButtons()) {
//					b.setClicked(false);
//				}
//
//				for (TextField t : currentGroup.getTextFields()) {
//					if (!t.isInBounds(e.getX(), e.getY()))
//						t.unFocus();
//				}
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				for (Button b : currentGroup.getButtons()) {
//					if (b.isInBounds(e.getX(), e.getY())) {
//						b.setClicked(true);
//					}
//				}
//				for (Button b : menu.getButtons()) {
//					if (b.isInBounds(e.getX(), e.getY())) {
//						b.setClicked(true);
//					}
//				}
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//			}
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//			}
//		});
//		addMouseMotionListener(new MouseMotionListener() {
//
//			@Override
//			public void mouseMoved(MouseEvent e) {
//				for (Button b : currentGroup.getButtons()) {
//					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
//				}
//
//				for (Button b : menu.getButtons()) {
//					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
//				}
//			}
//
//			@Override
//			public void mouseDragged(MouseEvent e) {
//				for (Button b : currentGroup.getButtons()) {
//					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
//				}
//
//				for (Button b : menu.getButtons()) {
//					b.setFocussed(b.isInBounds(e.getX(), e.getY()));
//				}
//			}
//		});
//		addKeyListener(new KeyListener() {
//
//			@Override
//			public void keyTyped(KeyEvent e) {
//				if (e.getKeyChar() == '\t') {
//					for (int i = 0; i < currentGroup.getTextFields().size(); i++) {
//						if (currentGroup.getTextFields().get(i).write) {
//							currentGroup.getTextFields().get(i).write = false;
//							currentGroup.getTextFields()
//									.get((i + 1) % currentGroup.getTextFields().size()).write = true;
//							break;
//						}
//					}
//				} else {
//					for (TextField t : currentGroup.getTextFields()) {
//						t.input("" + e.getKeyChar());
//					}
//				}
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//			}
//
//			@Override
//			public void keyPressed(KeyEvent e) {
//				if(e.getKeyCode() == 37) {
//					for (int i = 0; i < currentGroup.getTextFields().size(); i++) {
//						if (currentGroup.getTextFields().get(i).write) {
//							currentGroup.getTextFields().get(i).moveCusorBack(e.isControlDown(), e.isShiftDown());
//							break;
//						}
//					}
//					return;
//				}
//				if(e.getKeyCode() == 39) {
//					for (int i = 0; i < currentGroup.getTextFields().size(); i++) {
//						if (currentGroup.getTextFields().get(i).write) {
//							currentGroup.getTextFields().get(i).moveCusorForwards(e.isControlDown(), e.isShiftDown());
//							break;
//						}
//					}
//					return;
//				}
//			}
//		});
//
//		this.setMinimumSize(new Dimension(510, 400));
//		
//		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
//
//		setVisible(true);

		this.client = client;

		menu = new Menu(this);
		addFixed(menu);

		mainPage = new MainPage(this);
		add(mainPage);
		
		showGroups(new int[] {0});

//		menu.update(this, new Rectangle(200, this.getHeight()));
		update();
		
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

//	public void showGroup(int i) {
//		currentGroup = groups.get(i);
//	}

	public void update() {
		super.update();
//		for (Group group : groups) {
//			group.update(this, new Rectangle(200, 0, this.getWidth()-200, this.getHeight()));
//		}

		if (!client.receivedContacts)
			return;
		
//		System.out.println("Test");

		client.receivedContacts = false;

//		groups = new CopyOnWriteArrayList<>();
		keepGroups(new int[] {0});

//		groups.add(mainPage);

		String[] contacts = client.getContacts();

		for (String name : contacts) {
			add(new Chat(client, name));
		}
		menu.update(this, new Rectangle(200, this.getHeight()));
	}

//	public void draw() {
//		BufferStrategy bs = getBufferStrategy();
//
//		if (bs == null) {
//			createBufferStrategy(3);
//			return;
//		}
//		Graphics g;
//		
//		try {
//			g = bs.getDrawGraphics();
//		} catch (IllegalStateException ise) {
//			ise.printStackTrace();
//			return;
//		}
//		
//		g.clearRect(0, 0, getWidth(), getHeight());
//
//		currentGroup.draw(g);
//
//		menu.draw(g);
//
//		drawCycle++;
//		drawCycle %= 100;
//		if (drawCycle == 0) {
//			update();
//		}
//
//		g.dispose();
//
//		try {
//			bs.show();
//		}catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//	}

}

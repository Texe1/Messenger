package gui.general;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Button;
import gui.drawable.TextField;
import gui.drawable.group.Group;

public class Frame extends java.awt.Frame {

	private static final long serialVersionUID = 4350277027834712246L;

	private CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<Group> fixedGroups = new CopyOnWriteArrayList<>();
	private Group[] currentGroups = new Group[0];
	
	private boolean draw = true;
	
	private Thread drawThread = new Thread() {
		@Override
		public void run() {
			while (draw) {
				draw();
			}
		}
	};
	
	Rectangle drawingRect;

	public Frame(int width, int height) {
		drawingRect = new Rectangle(0, 0, width, height);
		this.setSize(width, height);
		this.setLocationRelativeTo(null);
		this.setBackground(Color.DARK_GRAY.darker());
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {}
			
			@Override
			public void componentResized(ComponentEvent e) {
				drawingRect = new Rectangle(200, 0, getWidth()-200, getHeight());
				update(true);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {}
			
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {
				draw = false;
				System.out.println("Iconified");
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				draw = true;
				drawThread = new Thread() {
					@Override
					public void run() {
						while(draw) {
							draw();
						}
					}
				};
				drawThread.start();
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				for (Group g : currentGroups) {
					for (Button b : g.getButtons()) {
						b.setClicked(false);
					}
					for (TextField t : g.getTextFields()) {
						if (!t.isInBounds(e.getX(), e.getY()))
							t.unFocus();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				for (Group g : currentGroups) {
					for (Button b : g.getButtons()) {
						if (b.isInBounds(e.getX(), e.getY())) {
							b.setClicked(true);
						}
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
				for (Group g : currentGroups) {
					for (Button b : g.getButtons()) {
						b.setFocussed(b.isInBounds(e.getX(), e.getY()));
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				for (Group g : currentGroups) {
					for (Button b : g.getButtons()) {
						b.setFocussed(b.isInBounds(e.getX(), e.getY()));
					}
				}
			}
		});
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\t') {
					boolean input = false;
					for (Group g : currentGroups) {
						for (int i = 0; i < g.getTextFields().size(); i++) {
							if (g.getTextFields().get(i).write) {
								g.getTextFields().get(i).write = false;
								g.getTextFields().get((i + 1) % g.getTextFields().size()).write = true;

								input = true;
								break;
							}
						}

						if (input)
							break;
					}
				} else {
					for (Group g : currentGroups) {
						for (TextField t : g.getTextFields()) {
							t.input("" + e.getKeyChar());
						}
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 37) {
					boolean input = false;
					for (Group g : currentGroups) {
						for (int i = 0; i < g.getTextFields().size(); i++) {
							if (g.getTextFields().get(i).write) {
								g.getTextFields().get(i).moveCusorBack(e.isControlDown(), e.isShiftDown());

								input = true;
								break;
							}
						}
						if (input)
							break;
					}
					return;
				}
				if (e.getKeyCode() == 39) {
					boolean input = false;
					for (Group g : currentGroups) {
						for (int i = 0; i < g.getTextFields().size(); i++) {
							if (g.getTextFields().get(i).write) {
								g.getTextFields().get(i).moveCusorForwards(e.isControlDown(), e.isShiftDown());
								input = true;
								break;
							}
						}
						if (input)
							break;
					}
					return;
				}
			}
		});

		this.setMinimumSize(new Dimension(510, 400));

		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());

		setVisible(true);
		
		
		createBufferStrategy(3);

		drawThread.start();
		
		update(true);

	}
	
	public synchronized void showGroups(int[] is) {
		currentGroups = new Group[is.length+fixedGroups.size()];
		for (int i = 0; i < is.length; i++) {
			currentGroups[i] = groups.get(is[i]);
		}
		for (int i = 0; i < fixedGroups.size(); i++) {
			currentGroups[i+is.length] = fixedGroups.get(i);
		}
	}
	
	public synchronized Group[] getAllGroups() {
		Group[] ret = new Group[groups.size() + fixedGroups.size()];
		
		for (int i = 0; i < fixedGroups.size(); i++) {
			ret[i] = fixedGroups.get(i);
		}
		
		for (int i = 0; i < groups.size(); i++) {
			ret[i + fixedGroups.size()] = groups.get(i);
		}
		
		return ret;
	}
	
	public Group[] getGroups() {
		Group[] ret = new Group[groups.size()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = groups.get(i);
		}
		
		return ret;
	}
	
	public Group[] getFixedGroups() {
		Group[] ret = new Group[groups.size()];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = groups.get(i);
		}
		
		return ret;
	}
	
	public void add(Group g) {
		groups.add(g);
	}
	public void addFixed(Group g) {
		fixedGroups.add(g);
	}
	
	public void update(boolean forceUpdate) {
		for (Group group : currentGroups) {
			group.update(this, drawingRect, forceUpdate);	
		}
	}
	
	public void draw() {
		BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g;
		
		try {
			g = bs.getDrawGraphics();
		} catch (IllegalStateException ise) {
//			ise.printStackTrace();
			return;
		}
		
		g.clearRect(0, 0, getWidth(), getHeight());

		for (Group group : currentGroups) {
			group.draw(g);
		}

		g.dispose();

		try {
			bs.show();
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void showGroup(Group g) {
		for (int i = 0; i < groups.size(); i++) {
			if(groups.get(i).name.equals(g.name)) {
				showGroups(new int[] {i});
				break;
			}
		}
	}

	public void keepGroups(int[] indices){
		CopyOnWriteArrayList<Group> keptGroups = new CopyOnWriteArrayList<>();
		
		for (int i = 0; i < indices.length; i++) {
			keptGroups.add(groups.get(indices[i]));
		}
		
		groups = keptGroups;
	}
	
	
}

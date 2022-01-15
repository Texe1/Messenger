package gui.drawable.group;

import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Button;
import gui.general.Frame;

public class Menu extends Group{
	
	private CopyOnWriteArrayList<String> pages = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<MenuButton> menuButtons = new CopyOnWriteArrayList<>();
	
	Frame frame;
	
	public Menu(Frame f) {
		super(-200f, 0f, 200f, 1f);
		setCoordType(2, CoordType.ABS);
		setCoordType(3, CoordType.REL);
		this.frame = f;
		this.restrictUpdate = false;
		update(f, f.getBounds(), false);
		this.s = "Menu";
	}
	
	@Override
	public void update(Frame frame, Rectangle r, boolean forceUpdate) {
		super.update(frame, r, forceUpdate);
		
		
		// adding new Pages
		for (int i = 0; i < frame.getAllGroups().length; i++) {
			Group group = frame.getAllGroups()[i];
			if(group.name == Group.NAME_GENERIC) {
				continue;
			}
			
			if(pages.contains(group.name)) continue;
			
			addPage(group.name, group);
			
		}
		
		// removing old pages
		Group[] groups = frame.getAllGroups();
		boolean isStillActive = false;
		for (String s : pages) {
			isStillActive = false;
			for (Group group : groups) {
				if(group.name.equals(s)) {
					isStillActive = true;
					break;
				}
			}
			if(!isStillActive) {
				removePage(s);
			}
		}
	}
	
	public synchronized void addPage(String s, Group g) {
		pages.add(s);
		
		MenuButton mb = new MenuButton(getCoords()[1] + 60 + 40 * pages.size(), s, pages.size()-1) {
			
			@Override
			public void run() {
				frame.showGroup(g);
				frame.update(false);
			}
		};
		
		menuButtons.add(mb);
		add(mb);
	}
	
	public synchronized void removePage(String s) {
		int i = pages.indexOf(s);
		
		MenuButton mb = menuButtons.remove(i);
		pages.remove(i);
		
		buttons.remove(mb);
		drawables.remove(mb);
	}
	
	public static abstract class MenuButton extends Button{
		
		int i;
		
		public MenuButton(float y, String name, int i) {
			super(0f, y, 1f, 40f);
			setCoordType(2, CoordType.REL);
			
			this.setText(name);
			this.i = i;
		}
	}
}

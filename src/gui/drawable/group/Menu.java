package gui.drawable.group;

import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.Frame;
import gui.drawable.Button;

public class Menu extends Group{
	
	Rectangle r;
	
	Frame f;
	
	public Menu(int x, int y, int width, int height, Frame f) {
		this.r = new Rectangle(x, y, width, height);
		this.f = f;
		
		update();
	}
	
	@Override
	public void update() {
		int y = r.y;
		
		this.buttons = new CopyOnWriteArrayList<>();
		this.drawables = new CopyOnWriteArrayList<>();
		
		for (int i = 0; i < f.getGroups().size(); i++) {
			Group group = f.getGroups().get(i);
			
			MenuButton b = new MenuButton(r.x, y, r.width, 40, i, group.name) {
				
				@Override
				public void run() {
					f.showGroup(i);
					f.getGroups().get(i).update();
				}
			};
			
			y += 40;
			
			b.edgeRadius = 0;
			
			this.add(b);
			
		}
	}
	
	public static abstract class MenuButton extends Button{
		
		int i;
		
		public MenuButton(int x, int y, int width, int height, int i, String name) {
			super(x, y, width, height);
			
			this.text = name;
			this.i = i;
		}
	}
}

package gui.drawable.group;

import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Button;
import gui.general.Frame;

public class Menu extends Group{
	
	public Menu(Frame f) {
		super(-200f, 0f, 200f, 1f);
		setCoordType(2, CoordType.ABS);
		setCoordType(3, CoordType.REL);
		update(f, f.getBounds());
	}
	
	@Override
	public void update(Frame frame, Rectangle r) {
		super.update(frame, r);
		float y = getCoords()[1] + 100;
		
		this.buttons = new CopyOnWriteArrayList<>();
		this.drawables = new CopyOnWriteArrayList<>();
		
		for (int i = 0; i < frame.getGroups().length; i++) {
			Group group = frame.getGroups()[i];
			if(group.name == Group.NAME_GENERIC) {
				continue;
			}
			
			MenuButton b = new MenuButton(y, group.name, i) {
				
				@Override
				public void run() {
					frame.showGroups(new int[] {i});
					frame.update();
				}
			};
			
			y += 40;
			
			b.edgeRadius = 0;
			
			b.setText(group.name);
			b.update(frame, absoluteCoords);
			this.add(b);
			
		}
	}
	
	public static abstract class MenuButton extends Button{
		
		int i;
		
		public MenuButton(float y, String name, int i) {
			super(0f, y, 1f, 40f);
			setCoordType(2, CoordType.REL);
			
			this.text = name;
			this.i = i;
		}
	}
}

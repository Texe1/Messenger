package gui.drawable.group;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Button;
import gui.drawable.Drawable;
import gui.drawable.TextField;
import gui.general.Frame;

public class Group extends Drawable{
	
	public static final String NAME_GENERIC = "generic";
	
	protected CopyOnWriteArrayList<Drawable> drawables = new CopyOnWriteArrayList<>();
	protected CopyOnWriteArrayList<Button> buttons = new CopyOnWriteArrayList<>();
	protected CopyOnWriteArrayList<TextField> textFields = new CopyOnWriteArrayList<>();
	
	protected Dimension minSize = new Dimension(0, 0);
	
	public Group() {
		super(.025f, .025f, .95f, .95f);
		setCoordType(0, CoordType.REL);
		setCoordType(1, CoordType.REL);
		setCoordType(2, CoordType.REL);
		setCoordType(3, CoordType.REL);
		this.s = "Group";
	}
	
	public Group(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	public String name = NAME_GENERIC;
	
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

	public CopyOnWriteArrayList<Drawable> getDrawables() {
		return drawables;
	}

	public CopyOnWriteArrayList<Button> getButtons() {
		return buttons;
	}

	public CopyOnWriteArrayList<TextField> getTextFields() {
		return textFields;
	}
	
	@Override
	public void draw(Graphics g) {
		for (Drawable d : drawables) {
			d.draw(g);
		}
	}
	
	@Override
	public void update(Frame f, Rectangle r, boolean forceUpdate) {
		super.update(f, r, forceUpdate);
		for (Drawable d : drawables) {
			d.update(f, absoluteCoords, forceUpdate);
			d.lastRefRect = absoluteCoords;
		}
	}
}

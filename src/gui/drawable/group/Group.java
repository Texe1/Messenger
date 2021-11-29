package gui.drawable.group;

import java.awt.Graphics;
import java.util.concurrent.CopyOnWriteArrayList;

import gui.drawable.Button;
import gui.drawable.Drawable;
import gui.drawable.TextField;

public class Group extends Drawable{
	protected CopyOnWriteArrayList<Drawable> drawables = new CopyOnWriteArrayList<>();
	protected CopyOnWriteArrayList<Button> buttons = new CopyOnWriteArrayList<>();
	protected CopyOnWriteArrayList<TextField> textFields = new CopyOnWriteArrayList<>();
	
	public String name = "Anonymus";
	
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
	
	public void update() {}
}

package gui.drawable;

import java.util.concurrent.CopyOnWriteArrayList;

public class Group {
	protected CopyOnWriteArrayList<Drawable> drawables = new CopyOnWriteArrayList<>();
	protected CopyOnWriteArrayList<Button> buttons = new CopyOnWriteArrayList<>();
	protected CopyOnWriteArrayList<TextField> textFields = new CopyOnWriteArrayList<>();
	
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
}

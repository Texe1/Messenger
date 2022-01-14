package gui.drawable;

import java.awt.Graphics;
import java.awt.Rectangle;

import gui.general.Frame;

public abstract class Drawable {
	boolean dead = false;
	public boolean draw = true;

	public Rectangle lastRefRect = new Rectangle(0, 0, 0, 0);

	public boolean restrictUpdate = true;

	protected String s = NAME_DEFAULT;
	public static final String NAME_DEFAULT = "Drawable";

	public Rectangle absoluteCoords = new Rectangle(0, 0, 0, 0);
	private float[] scaledSizes = new float[] { 0, 0, 0, 0 };
	private CoordType[] coordTypes = new CoordType[] { CoordType.ABS, CoordType.ABS, CoordType.ABS, CoordType.ABS };

	public Drawable(int x, int y, int width, int height) {
		absoluteCoords = new Rectangle(x, y, width, height);
	}

	public Drawable(float x, float y, float width, float height) {
		scaledSizes = new float[] { x, y, width, height };
	}

	public boolean isDead() {
		return dead;
	}

	public void setCoordType(int i, CoordType ct) {
		if (i < 4 && i >= 0) {
			coordTypes[i] = ct;
		}
	}

	public abstract void draw(Graphics g);

	public void update(Frame f, Rectangle r, boolean forceUpdate) {
		if (!forceUpdate && lastRefRect.equals(r) && restrictUpdate) {
			return;
		}

//		int oldWidth = absoluteCoords.width;
		
		absoluteCoords.x = Math.round(scaledSizes[0]) + r.x;
		absoluteCoords.y = Math.round(scaledSizes[1]) + r.y;
		absoluteCoords.width = Math.round(scaledSizes[2]);
		absoluteCoords.height = Math.round(scaledSizes[3]);

		switch (coordTypes[0]) {
		case ABS:// coordinate 0 is absolute
			break;
		case REL:// coordinate 0 is relative to the width of the container
			absoluteCoords.x = Math.round(scaledSizes[0] * r.width) + r.x;
			break;
		case DIST:// coordinate 0 defines the distance between the component's left edge and the
					// container's right edge
			absoluteCoords.x = r.x + r.width - Math.round(scaledSizes[0]);
			break;
		case DISTMID:
			absoluteCoords.x = r.x + r.width / 2 - Math.round(scaledSizes[0]);
			break;
		}
		switch (coordTypes[1]) {
		case ABS:// coordinate 1 is absolute
			break;
		case REL:// coordinate 1 is relative to the height of the container
			absoluteCoords.y = Math.round(scaledSizes[1] * r.height) + r.y;
			break;
		case DIST:// coordinate 1 defines the distance between the component's upper edge and the
					// container's lower edge
			absoluteCoords.y = r.y + r.height - Math.round(scaledSizes[1]);
			break;
		case DISTMID:
			absoluteCoords.y = r.y + r.height / 2 - Math.round(scaledSizes[1]);
			break;
		}
		switch (coordTypes[2]) {
		case ABS:// coordinate 2 is absolute
			break;
		case REL:// coordinate 2 is relative to the width of the container
			absoluteCoords.width = Math.round(scaledSizes[2] * r.width);
//			if(s.equals("ClientMainPage") && oldWidth != absoluteCoords.width)System.out.println("[2]rel|" + oldWidth + "=>" + absoluteCoords.width);
			break;
		case DIST:// coordinate 2 defines the distance between the component's right edge and the
					// container's right edge
			absoluteCoords.width = r.x + r.width - Math.round(scaledSizes[2]) - absoluteCoords.x;
			break;
		case DISTMID:
			absoluteCoords.width = r.x + r.width / 2 - Math.round(scaledSizes[2]) - absoluteCoords.x;
			break;
		}
		switch (coordTypes[3]) {
		case ABS:// coordinate 3 is absolute
			break;
		case REL:// coordinate 3 is relative to the height of the container
			absoluteCoords.height = Math.round(scaledSizes[3] * r.height);
			break;
		case DIST:// coordinate 3 defines the distance between the component's lower edge and the
					// container's lower edge
			absoluteCoords.height = r.y + r.height - Math.round(scaledSizes[3]) - absoluteCoords.y;
			break;
		case DISTMID:
			absoluteCoords.height = r.y + r.height / 2 - Math.round(scaledSizes[3]) - absoluteCoords.y;
			break;
		}

		if (s.equals("Debug")) {
			System.out.println(absoluteCoords.x + "," + absoluteCoords.y + "," + absoluteCoords.width + ","
					+ absoluteCoords.height);
		}
		
	}

	public void setCoords(float x, float y, float width, float height) {
		scaledSizes[0] = x;
		scaledSizes[1] = y;
		scaledSizes[2] = width;
		scaledSizes[3] = height;
	}

	public float[] getCoords() {
		return scaledSizes;
	}

	public static enum CoordType {
		ABS, REL, DIST, DISTMID
	}

}

package name.herve.networktycoon.gui;

import name.herve.bastod.tools.math.Dimension;
import name.herve.bastod.tools.math.Point;

public class BoardGuiTool {
	private double ratio;
	private int xOffset;
	private int yOffset;

	public BoardGuiTool(Dimension board, Dimension screen) {
		this(board, screen, 0, 0, 0, 0);
	}

	public BoardGuiTool(Dimension board, Dimension screen, int reservedTop, int reservedBottom, int reservedLeft, int reservedRight) {
		super();

		double screenWidth = screen.getW() - reservedLeft - reservedRight;
		double screenHeigth = screen.getH() - reservedTop - reservedBottom;

		double xr = screenWidth / board.getW();
		double yr = screenHeigth / board.getH();
		ratio = Math.min(xr, yr);

		xOffset = reservedLeft + (int) ((screenWidth - (board.getW() * ratio)) / 2);
		yOffset = reservedTop + (int) ((screenHeigth - (board.getH() * ratio)) / 2);
	}

	public Point boardPointToScreen(Point p) {
		return new Point((int) boardXCoordToScreen(p.getX()), (int) boardYCoordToScreen(p.getY()));
	}

	public double boardXCoordToScreen(double coord) {
		return xCoordToScreen(coord + 0.5);
	}

	public double boardYCoordToScreen(double coord) {
		return yCoordToScreen(coord + 0.5);
	}

	public Dimension dimToScreen(Dimension d) {
		return new Dimension((int) dimToScreen(d.getW()), (int) (dimToScreen(d.getH())));
	}

	public double dimToScreen(double length) {
		return length * ratio;
	}

	public Point pointToScreen(Point p) {
		return new Point((int) xCoordToScreen(p.getX()), (int) yCoordToScreen(p.getY()));
	}

	public double xCoordToScreen(double coord) {
		return (coord * ratio) + xOffset;
	}

	public double yCoordToScreen(double coord) {
		return (coord * ratio) + yOffset;
	}
}

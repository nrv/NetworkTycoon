package name.herve.networktycoon.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import name.herve.bastod.tools.math.Dimension;
import name.herve.bastod.tools.math.Point;
import name.herve.bastod.tools.math.Vector;
import name.herve.networktycoon.Board;
import name.herve.networktycoon.Connection;
import name.herve.networktycoon.Node;

public class BoardGuiTool {
	private final static RenderingHints HINTS;

	static {
		HINTS = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		HINTS.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		HINTS.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	}

	private double ratio;
	private int xOffset;
	private int yOffset;
	private Dimension screen;

	public BoardGuiTool(Dimension board, Dimension screen) {
		this(board, screen, 0, 0, 0, 0);
	}

	public BoardGuiTool(Dimension board, Dimension screen, int reservedTop, int reservedBottom, int reservedLeft, int reservedRight) {
		super();

		this.screen = screen;

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

	public BufferedImage debug(Board b) {
		BufferedImage img = new BufferedImage(screen.getW(), screen.getH(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setRenderingHints(HINTS);

		Font font = new Font("Sanserif", Font.BOLD, 12);
		g2.setFont(font);

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, screen.getW(), screen.getH());

		// g2.setColor(Color.GREEN);
		// for (int l = 0; l <= b.getH(); l++) {
		// Point p1 = tool.pointToScreen(new Point(0, l));
		// Point p2 = tool.pointToScreen(new Point(b.getW(), l));
		// g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		// }
		// for (int c = 0; c <= b.getW(); c++) {
		// Point p1 = tool.pointToScreen(new Point(c, 0));
		// Point p2 = tool.pointToScreen(new Point(c, b.getH()));
		// g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		// }

		for (Connection c : b.getNetwork().getConnections()) {
			g2.setColor(Color.BLUE);
			Iterator<Node> itn = c.iterator();
			Point p1 = boardPointToScreen(itn.next().getCoord());
			Point p2 = boardPointToScreen(itn.next().getCoord());
			g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

			Vector v1 = new Vector(p1.getX(), p1.getY());
			Vector v2 = new Vector(p2.getX(), p2.getY());
			Vector v12 = v2.copy().remove(v1).multiply(0.5f);
			v1.add(v12);
			g2.setColor(Color.BLACK);
			g2.drawString(c.getNbResourceNeeded() + "(" + c.getNbPath() + ")", v1.getX(), v1.getY());
		}

		double radius = 2.5;
		for (Node n : b.getNetwork()) {
			g2.setColor(Color.RED);
			Shape circle = new Ellipse2D.Double(boardXCoordToScreen(n.getX() - radius), boardYCoordToScreen(n.getY() - radius), dimToScreen(2.0 * radius), dimToScreen(2.0 * radius));
			g2.fill(circle);
			g2.setColor(Color.BLACK);
			Point p1 = boardPointToScreen(n.getCoord());
			g2.drawString(n.getName(), p1.getX(), p1.getY());
		}

		return img;
	}

	public void debug(Board b, File f) throws IOException {
		ImageIO.write(debug(b), "jpeg", f);
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

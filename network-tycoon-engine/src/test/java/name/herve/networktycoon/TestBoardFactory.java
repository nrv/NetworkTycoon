package name.herve.networktycoon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import name.herve.bastod.tools.math.Dimension;
import name.herve.bastod.tools.math.Point;
import name.herve.networktycoon.delaunay.Pnt;
import name.herve.networktycoon.delaunay.Triangle;
import name.herve.networktycoon.delaunay.Triangulation;
import name.herve.networktycoon.gui.BoardGuiTool;

public class TestBoardFactory {
	private final static RenderingHints HINTS;

	static {
		HINTS = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		HINTS.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		HINTS.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	}

	public static void main(String[] args) {
		Dimension[] screens = new Dimension[] { new Dimension(640, 480), new Dimension(1024, 768), new Dimension(1280, 1024), new Dimension(1920, 1080) };
		int rb = 200;
		int rr = 100;
		File tempDir = new File("/tmp");

		try {
			BoardFactory bf = new BoardFactory();
			Board b = bf.getRandomBoard();

			int top = -2 * b.getH();
			int bottom = 2 * b.getH();
			int middle = b.getW() / 2;
			int left = -b.getW();
			int right = 2 * b.getW();
			Triangulation delaunay = new Triangulation(new Triangle(new Pnt(left, bottom).setOutsideWorld(true), new Pnt(right, bottom).setOutsideWorld(true), new Pnt(middle, top).setOutsideWorld(true)));

			for (Node n : b.getNetwork()) {
				delaunay.delaunayPlace(new Pnt(n.getX(), n.getY()));
			}

			for (Dimension s : screens) {
				for (boolean doReserve : new boolean[] { false, true }) {
					BoardGuiTool tool = null;

					if (doReserve) {
						tool = new BoardGuiTool(b.getDimension(), s, 0, rb, 0, rr);
					} else {
						tool = new BoardGuiTool(b.getDimension(), s);
					}

					BufferedImage img = new BufferedImage(s.getW(), s.getH(), BufferedImage.TYPE_INT_RGB);
					Graphics2D g2 = (Graphics2D) img.getGraphics();
					g2.setRenderingHints(HINTS);

					g2.setColor(Color.WHITE);
					g2.fillRect(0, 0, s.getW(), s.getH());

					g2.setColor(Color.BLUE);
					for (int l = 0; l <= b.getH(); l++) {
						Point p1 = tool.pointToScreen(new Point(0, l));
						Point p2 = tool.pointToScreen(new Point(b.getW(), l));
						g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
					}
					for (int c = 0; c <= b.getW(); c++) {
						Point p1 = tool.pointToScreen(new Point(c, 0));
						Point p2 = tool.pointToScreen(new Point(c, b.getH()));
						g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
					}

					g2.setColor(Color.GREEN);
					for (Triangle triangle : delaunay) {
						Pnt[] vertices = triangle.toArray(new Pnt[0]);

						for (int i = 0; i < vertices.length; i++) {
							int j = i == vertices.length - 1 ? 0 : i + 1;
							if (!vertices[i].isOutsideWorld() && !vertices[j].isOutsideWorld()) {
								Point p1 = tool.boardPointToScreen(new Point((int) vertices[i].coord(0), (int) vertices[i].coord(1)));
								Point p2 = tool.boardPointToScreen(new Point((int) vertices[j].coord(0), (int) vertices[j].coord(1)));
								g2.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
							}
						}
					}

					g2.setColor(Color.RED);
					double radius = 0.25;
					for (Node n : b.getNetwork()) {
						Shape circle = new Ellipse2D.Double(tool.boardXCoordToScreen(n.getX() - radius), tool.boardYCoordToScreen(n.getY() - radius), tool.dimToScreen(2.0 * radius), tool.dimToScreen(2.0 * radius));
						g2.fill(circle);
					}

					if (doReserve) {
						g2.setColor(Color.GRAY);
						g2.fill(new Rectangle(0, s.getH() - rb, s.getW(), rb));
						g2.fill(new Rectangle(s.getW() - rr, 0, rr, s.getH()));
					}

					ImageIO.write(img, "jpeg", new File(tempDir, "tbf_" + (doReserve ? "x" : "") + "_" + s.getW() + "x" + s.getH() + ".jpg"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

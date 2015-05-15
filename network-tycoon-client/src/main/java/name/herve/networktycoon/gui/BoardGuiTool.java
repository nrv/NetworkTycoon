/*
 * Copyright 2015 Nicolas HERVE
 *
 * This file is part of Network Tycoon.
 *
 * Network Tycoon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Network Tycoon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Network Tycoon. If not, see <http://www.gnu.org/licenses/>.
 */
package name.herve.networktycoon.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import name.herve.bastod.tools.math.Dimension;
import name.herve.bastod.tools.math.Point;
import name.herve.bastod.tools.math.Vector;
import name.herve.networktycoon.Board;
import name.herve.networktycoon.Connection;
import name.herve.networktycoon.ConnectionElement;
import name.herve.networktycoon.Node;
import name.herve.networktycoon.ResourceType;

/**
 * @author Nicolas HERVE
 */
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
		this(board, screen, false);
	}

	public BoardGuiTool(Dimension board, Dimension screen, boolean center) {
		this(board, screen, 0, 0, 0, 0, center);
	}

	public BoardGuiTool(Dimension board, Dimension screen, int reservedTop, int reservedBottom, int reservedLeft, int reservedRight, boolean center) {
		super();

		this.screen = screen;

		double screenWidth = screen.getW() - reservedLeft - reservedRight;
		double screenHeigth = screen.getH() - reservedTop - reservedBottom;

		double xr = screenWidth / board.getW();
		double yr = screenHeigth / board.getH();
		ratio = Math.min(xr, yr);

		xOffset = reservedLeft + (int) ((screenWidth - (board.getW() * ratio)) / 2);
		yOffset = reservedTop + (int) ((screenHeigth - (board.getH() * ratio)) / 2);

		if (center) {
			xOffset -= screenWidth / 2;
			yOffset -= screenHeigth / 2;
		}
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

		Shape rectangle = new Rectangle2D.Double(0, 0, dimToScreen(4), dimToScreen(1));
		for (Connection c : b.getNetwork().getConnections()) {
			g2.setColor(Color.BLUE);
			int x1 = (int) boardXCoordToScreen(c.getNode1().getX());
			int y1 = (int) boardYCoordToScreen(c.getNode1().getY());
			int x2 = (int) boardXCoordToScreen(c.getNode2().getX());
			int y2 = (int) boardYCoordToScreen(c.getNode2().getY());
			g2.drawLine(x1, y1, x2, y2);

			Vector v1 = new Vector(x1, y1);
			Vector v2 = new Vector(x2, y2);
			Vector v12 = v2.copy().remove(v1).multiply(0.5f);
			v1.add(v12);
			g2.setColor(Color.BLACK);
			StringBuilder sb = new StringBuilder();
			sb.append(c.getNbResourceNeeded());
			sb.append("(");
			sb.append(c.getNbPath());
			for (ResourceType rt : c.getResourceTypes()) {
				sb.append(", ");
				sb.append(rt.getCode());
			}
			sb.append(")");
			g2.drawString(sb.toString(), v1.getX(), v1.getY());
			
			AffineTransform saveXform = g2.getTransform();
			for (ResourceType rt : c.getResourceTypes()) {
				for (ConnectionElement ce : c.getConnectionElements(rt)) {
					AffineTransform at = new AffineTransform();
					at.translate(boardXCoordToScreen(ce.getX() - 2), boardYCoordToScreen(ce.getY() - 0.5));
					g2.setTransform(at);
					g2.fill(rectangle);
				}
			}
			g2.setTransform(saveXform);
		}

		double radius = 2.5;
		Shape circle = new Ellipse2D.Double(0, 0, dimToScreen(2.0 * radius), dimToScreen(2.0 * radius));
		AffineTransform saveXform = g2.getTransform();
		for (Node n : b.getNetwork()) {
			g2.setColor(Color.RED);
			AffineTransform at = new AffineTransform();
			at.translate(boardXCoordToScreen(n.getX() - radius), boardYCoordToScreen(n.getY() - radius));
			g2.setTransform(at);
			g2.fill(circle);
			
			g2.setColor(Color.BLACK);
			at = new AffineTransform();
			at.translate(boardXCoordToScreen(n.getX()), boardYCoordToScreen(n.getY()));
			g2.setTransform(at);
			g2.drawString(n.getName(), 0, 0);
		}
		g2.setTransform(saveXform);

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

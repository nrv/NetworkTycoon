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
import name.herve.networktycoon.Board;
import name.herve.networktycoon.BoardFactory;
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
		System.out.println("Drawing board " + b);
		BufferedImage img = new BufferedImage(screen.getW(), screen.getH(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setRenderingHints(HINTS);

		Font font = new Font("Sanserif", Font.BOLD, 12);
		g2.setFont(font);

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, screen.getW(), screen.getH());

		Shape rectangle = new Rectangle2D.Double(- dimToScreen(BoardFactory.GFX_CE_WIDTH / 2f), - dimToScreen(BoardFactory.GFX_CE_HEIGHT / 2f), dimToScreen(BoardFactory.GFX_CE_WIDTH), dimToScreen(BoardFactory.GFX_CE_HEIGHT));
		for (Connection c : b.getNetwork().getConnections()) {
			for (ResourceType rt : c.getResourceTypes()) {
				g2.setColor(rt.getColor());
				int px = (int)boardXCoordToScreen(c.getNode1().getX());
				int py = (int)boardYCoordToScreen(c.getNode1().getY());
				for (ConnectionElement ce : c.getConnectionElements(rt)) {
					int x = (int)boardXCoordToScreen(ce.getX());
					int y = (int)boardYCoordToScreen(ce.getY());
					g2.drawLine(px, py, x, y);
					px = x;
					py = y;
				}
				int x = (int)boardXCoordToScreen(c.getNode2().getX());
				int y = (int)boardYCoordToScreen(c.getNode2().getY());
				g2.drawLine(px, py, x, y);
				AffineTransform saveXform = g2.getTransform();
				for (ConnectionElement ce : c.getConnectionElements(rt)) {
					AffineTransform at = new AffineTransform();
					at.translate(boardXCoordToScreen(ce.getX()), boardYCoordToScreen(ce.getY()));
					at.rotate(ce.getO());
					g2.setTransform(at);
					g2.setColor(rt.getColor());
					g2.fill(rectangle);
					g2.setColor(Color.BLACK);
					g2.draw(rectangle);
				}
				g2.setTransform(saveXform);
			}
		}

		Shape circle = new Ellipse2D.Double(0, 0, dimToScreen(2f * BoardFactory.GFX_NODE_RADIUS), dimToScreen(2f * BoardFactory.GFX_NODE_RADIUS));
		AffineTransform saveXform = g2.getTransform();
		for (Node n : b.getNetwork()) {
			g2.setColor(Color.RED);
			AffineTransform at = new AffineTransform();
			at.translate(boardXCoordToScreen(n.getX() - BoardFactory.GFX_NODE_RADIUS), boardYCoordToScreen(n.getY() - BoardFactory.GFX_NODE_RADIUS));
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

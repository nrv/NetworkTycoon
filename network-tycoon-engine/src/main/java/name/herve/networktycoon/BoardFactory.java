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
package name.herve.networktycoon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import name.herve.bastod.tools.GameException;
import name.herve.bastod.tools.math.Dimension;
import name.herve.networktycoon.delaunay.Pnt;
import name.herve.networktycoon.delaunay.Triangle;
import name.herve.networktycoon.delaunay.Triangulation;

/**
 * @author Nicolas HERVE
 */
public class BoardFactory {
	private final static Dimension DEFAULT_BOARD_DIMENSION = new Dimension(20, 15);
	private final static double ANGLE_LIMIT = Math.PI / 6;
	private final static int NB_RESOURCES = 4;
	private final static int NB_NODES = 15;
	private final static int CLOSE_RANGE = 1;
	private final static int MAX_RANGE = 4;

	private final static String[] DEFAULT_RESOURCE_TYPES = new String[] { "*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" };

	private long seed;

	public BoardFactory() {
		super();
		setSeed(System.currentTimeMillis());
	}

	private Board createBoard() throws GameException {
		return new Board(DEFAULT_BOARD_DIMENSION);
	}

	public Board getEmptyBoard() throws GameException {
		Board board = createBoard();

		return board;
	}

	public Board getRandomBoard() throws GameException {
		Board board = getEmptyBoard();

		Random rd = new Random(seed);

		int nbResourceTypes = NB_RESOURCES + rd.nextInt(NB_RESOURCES / 2);
		int nbNode = NB_NODES + rd.nextInt(NB_NODES / 2);
		int noCloseNeighbourRange = CLOSE_RANGE;
		int maxNeighbourRange = MAX_RANGE;

		// Resources
		for (int r = 0; r < nbResourceTypes; r++) {
			ResourceType resourceType = new ResourceType(DEFAULT_RESOURCE_TYPES[r]);
			if (r == 0) {
				resourceType.setJocker(true);
			}
			board.registerResourceType(resourceType);
		}

		// Nodes
		int top = -5 * board.getH();
		int bottom = 2 * board.getH();
		int middle = board.getW() / 2;
		int left = -4 * board.getW();
		int right = 5 * board.getW();
		Triangulation triangulation = new Triangulation(new Triangle(new Pnt(left, bottom).setOutsideWorld(true), new Pnt(right, bottom).setOutsideWorld(true), new Pnt(middle, top).setOutsideWorld(true)));

		Network network = board.getNetwork();
		boolean[][] occupied = new boolean[board.getW()][board.getH()];
		for (int x = 0; x < board.getW(); x++) {
			Arrays.fill(occupied[x], false);
		}
		for (int n = 0; n < nbNode; n++) {
			Node node = new Node(n, "n" + n);
			int x, y;
			boolean ok = true;

			if (n == 0) {
				x = board.getW() / 2;
				y = board.getH() / 2;
			} else {
				do {
					ok = true;

					double tx = 0.5 + (rd.nextGaussian() / 3);
					double ty = 0.5 + (rd.nextGaussian() / 3);
					x = (int) Math.round(tx * board.getW());
					y = (int) Math.round(ty * board.getH());
					if ((x < 0) || (x >= board.getW()) || (y < 0) || (y >= board.getH())) {
						ok = false;
					}

					for (int nx = -noCloseNeighbourRange; (nx <= noCloseNeighbourRange) && ok; nx++) {
						for (int ny = -noCloseNeighbourRange; (ny <= noCloseNeighbourRange) && ok; ny++) {
							int rx = x + nx;
							int ry = y + ny;
							if ((rx >= 0) && (rx < board.getW()) && (ry >= 0) && (ry < board.getH())) {
								if (occupied[rx][ry]) {
									ok = false;
								}
							}
						}
					}

					if (ok) {
						boolean atLeastOneNeighbour = false;
						for (int nx = -maxNeighbourRange; (nx <= maxNeighbourRange) && !atLeastOneNeighbour; nx++) {
							for (int ny = -maxNeighbourRange; (ny <= maxNeighbourRange) && !atLeastOneNeighbour; ny++) {
								int rx = x + nx;
								int ry = y + ny;
								if ((rx >= 0) && (rx < board.getW()) && (ry >= 0) && (ry < board.getH())) {
									if (occupied[rx][ry]) {
										atLeastOneNeighbour = true;
									}
								}
							}
						}
						ok = atLeastOneNeighbour;
					}
				} while (!ok);
			}

			occupied[x][y] = true;
			node.setX(x);
			node.setY(y);
			network.addNode(node);
			triangulation.delaunayPlace(new Pnt(node));
		}

		// Connections
		for (Triangle triangle : triangulation) {
			Pnt[] vertices = triangle.toArray(new Pnt[0]);

			Node n1 = vertices[0].getNode();
			Node n2 = vertices[1].getNode();
			Node n3 = vertices[2].getNode();

			if ((n1 != null) && (n2 != null) && (n3 != null)) {
				tryToConnect(network, n1, n2, n3);
				tryToConnect(network, n1, n3, n2);
				tryToConnect(network, n3, n2, n1);
			}
		}

		List<Node> nodeToRemove = new ArrayList<Node>();
		do {
			nodeToRemove.clear();
			for (Node n : network) {
				if (n.getNbConnections() < 2) {
					nodeToRemove.add(n);
				}
			}
			for (Node n : nodeToRemove) {
				network.removeNode(n);
			}
		} while (!nodeToRemove.isEmpty());

		do {
			Connection c = network.getConnections().get(rd.nextInt(network.getNbConnections()));
			boolean remove = true;
			for (Node n : c) {
				if (n.getNbConnections() < 3) {
					remove = false;
				}
			}
			if (remove) {
				network.removeConnection(c);
			}
		} while (network.getNbConnections() > (2 * network.getNbNodes()));

		return board;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	private void tryToConnect(Network network, Node n1, Node n2, Node n3) {
		Pnt v12 = new Pnt(n1.getX() - n2.getX(), n1.getY() - n2.getY());
		Pnt v13 = new Pnt(n1.getX() - n3.getX(), n1.getY() - n3.getY());

		if (v12.angle(v13) > ANGLE_LIMIT) {
			if (!n1.isConnectedTo(n2)) {
				network.addConnection(n1, n2);
			}
			if (!n1.isConnectedTo(n3)) {
				network.addConnection(n1, n3);
			}
		}
	}
}

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import name.herve.bastod.tools.GameException;
import name.herve.bastod.tools.graph.Dijkstra;
import name.herve.bastod.tools.graph.Graph;
import name.herve.bastod.tools.math.Dimension;
import name.herve.bastod.tools.math.Vector;
import name.herve.networktycoon.delaunay.Pnt;
import name.herve.networktycoon.delaunay.Triangle;
import name.herve.networktycoon.delaunay.Triangulation;
import name.herve.networktycoon.gui.BoardGuiTool;

/**
 * @author Nicolas HERVE
 */
public class BoardFactory {
	private final static Dimension DEFAULT_BOARD_DIMENSION = new Dimension(200, 150);
	private final static double ANGLE_LIMIT = Math.PI / 6;
	private final static int NB_RESOURCES = 4;
	private final static int NB_NODES = 15;
	private final static int CLOSE_RANGE = 10;
	private final static int MAX_RANGE = 40;

	private final static String[] DEFAULT_RESOURCE_TYPES = new String[] { "*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" };

	private long seed;

	public BoardFactory() {
		super();
		setSeed(System.currentTimeMillis());
	}

	private Board createBoard() throws GameException {
		return new Board(DEFAULT_BOARD_DIMENSION);
	}

	private void doPathDuplicate(Board b) {
		Network network = b.getNetwork();
		Graph g = new Graph();
		Map<Node, name.herve.bastod.tools.graph.Node> map1 = new HashMap<Node, name.herve.bastod.tools.graph.Node>();
		Map<name.herve.bastod.tools.graph.Node, Node> map2 = new HashMap<name.herve.bastod.tools.graph.Node, Node>();
		Map<Connection, Integer> count = new HashMap<Connection, Integer>();
		for (Node n : network) {
			name.herve.bastod.tools.graph.Node nn = new name.herve.bastod.tools.graph.Node();
			map1.put(n, nn);
			map2.put(nn, n);
			g.addNode(nn);
		}
		for (Connection c : network.getConnections()) {
			g.addEdge(map1.get(c.getNode1()), map1.get(c.getNode2()), c.getNbResourceNeeded());
			count.put(c, 0);
		}
		
		Dijkstra dijkstra = new Dijkstra(g);
		for (int i = 0; i < network.getNbNodes() - 1; i++) {
			Node ni = network.getNodes().get(i);
			for (int j = i + 1; j < network.getNbNodes(); j++) {
				Node nj = network.getNodes().get(j);
				List<name.herve.bastod.tools.graph.Node> path = dijkstra.getPathNodes(map1.get(ni), map1.get(nj));

				Node p = ni;
				for (name.herve.bastod.tools.graph.Node cp : path) {
					Node n = map2.get(cp);
					Connection c = p.getConnectionTo(n);
					count.put(c, count.get(c) + 1);
					p = n;
				}
			}
		}
		
		int limit = (int)(0.08 * network.getNbNodes() * (network.getNbNodes() - 1) / 2);
		for (Connection c : network.getConnections()) {
//			System.out.println(c + " - " + count.get(c) + " / " + limit);
			if (count.get(c) >= limit) {
				c.setNbPath(2);
			}
		}
	}
	
	private void doGraphLayout(Board b) {
		Random rd = new Random(seed);
		
		BoardGuiTool debugTool = new BoardGuiTool(b.getDimension(), new Dimension(1024, 768));
		Network network = b.getNetwork();

		Map<Node, Vector> coord = new HashMap<Node, Vector>();
		for (Node u : network) {
			Vector cu = new Vector(u.getX(), u.getY());
			System.out.println("n " + u.getId() + " : " + cu);
			coord.put(u, cu);
		}
		Map<Connection, Float> springs = new HashMap<Connection, Float>();
		for (Connection c : network.getConnections()) {
			springs.put(c, (float) c.getNbResourceNeeded() * 15);
			// springs.put(c, (float) c.distance());
		}

		for (int i = 0; i < 100; i++) {
			try {
				debugTool.debug(b, new File("/tmp/nt/tbf_" + seed + "_" + i + ".jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			Map<Node, Vector> forces = new HashMap<Node, Vector>();
			for (Node u : network) {
				Vector cu = coord.get(u);
				Vector fu = new Vector(0, 0);
				forces.put(u, fu);
				for (Node v : network) {
					if (u == v) {
						continue;
					}
					Vector cv = coord.get(v);
					Vector fuv = cv.copy().remove(cu);
					// Vector fuv = cu.copy().remove(cv);
					float duv = fuv.length();
					if (duv == 0) {
						fuv = new Vector(10 * rd.nextFloat(), 10 * rd.nextFloat());
						duv = fuv.length();
					}
//					System.out.println(i + " : " + u.getId() + "<-" + v.getId() + "         ~ fuv 1 " + fuv + " (" + duv + ")");
					fuv.normalize();
					float force = 0;
					if (u.isConnectedTo(v)) {
						Connection c = u.getConnectionTo(v);
						float spring = springs.get(c);
						force = (float) (2 * Math.log10(spring)) * (duv - spring);
//						System.out.println(i + " : " + u.getId() + "<-" + v.getId() + "         ~ force c  " + force + " / " + spring);
					} else {
						force = 100 / (duv * duv);
//						System.out.println(i + " : " + u.getId() + "<-" + v.getId() + "         ~ force nc " + force);
					}

					fuv.multiply(force);
//					System.out.println(i + " : " + u.getId() + "<-" + v.getId() + "         ~ fuv 2 " + fuv);
					fu.add(fuv);
//					System.out.println();
				}
			}
			for (Node u : network) {
				Vector cu = coord.get(u);
				Vector fu = forces.get(u);
//				System.out.println(i + "~ f " + fu);
				cu.add(fu.multiply(0.1f));
//				System.out.println(i + "~ n " + u.getId() + " : " + cu);

				u.setX(cu.getXInt());
				u.setY(cu.getYInt());
			}
		}

		for (Node n : network) {
			Vector cn = coord.get(n);
//			System.out.println("n " + n.getId() + " : " + cn);
			n.setX(cn.getXInt());
			n.setY(cn.getYInt());
		}
	}

	public void doResources(Board board, int nbResourceTypes) {
		for (int r = 0; r < nbResourceTypes; r++) {
			ResourceType resourceType = new ResourceType(DEFAULT_RESOURCE_TYPES[r]);
			if (r == 0) {
				resourceType.setJocker(true);
			}
			board.registerResourceType(resourceType);
		}
	}

	private void doShuffle(Board b) {
		Random rd = new Random(seed);
		Network network = b.getNetwork();
		int x = b.getW() / 10;
		int y = b.getH() / 10;
		for (Node u : network) {
			u.setX(u.getX() + rd.nextInt(2 * x) - x);
			u.setY(u.getY() + rd.nextInt(2 * y) - y);
		}
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
		doResources(board, nbResourceTypes);

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
		} while (network.getNbConnections() > (1.8 * network.getNbNodes()));

		// Resources
		double maxDist = 0;
		for (Connection c : network.getConnections()) {
			double d = c.distance();
			if (d > maxDist) {
				maxDist = d;
			}
		}

		double d = 0;
		double r = 0;
		for (Connection c : network.getConnections()) {
			c.setNbResourceNeeded((int) Math.max(1, Math.floor((6 * c.distance()) / maxDist)));
			d += c.distance();
			r += c.getNbResourceNeeded();
		}

//		System.out.println("d = " + (d / network.getConnections().size()));
//		System.out.println("r = " + (r / network.getConnections().size()));
//		System.out.println("avg = " + (d / r));
		
//		doShuffle(board);
//		doGraphLayout(board);
		
		// Duplicate important path
		doPathDuplicate(board);
		
		return board;
	}

	public Board getSampleBoard() throws GameException {
		Board board = getEmptyBoard();

		doResources(board, 6);

		Network network = board.getNetwork();

		Node[] nodes = new Node[6];
		for (int n = 0; n < nodes.length; n++) {
			nodes[n] = new Node(n, "n" + n);
			network.addNode(nodes[n]);
		}

		nodes[0].setX(board.getW() / 2);
		nodes[0].setY((2 * board.getH()) / 6);

		nodes[1].setX(board.getW() / 4);
		nodes[1].setY(board.getH() / 6);

		nodes[2].setX((3 * board.getW()) / 4);
		nodes[2].setY(board.getH() / 6);

		network.addConnection(nodes[0], nodes[2]).setNbResourceNeeded(2);
		network.addConnection(nodes[0], nodes[1]).setNbResourceNeeded(2);
		network.addConnection(nodes[2], nodes[1]).setNbResourceNeeded(2);

		nodes[3].setX(board.getW() / 2);
		nodes[3].setY((4 * board.getH()) / 6);

		nodes[4].setX(board.getW() / 4);
		nodes[4].setY((5 * board.getH()) / 6);

		nodes[5].setX((3 * board.getW()) / 4);
		nodes[5].setY((5 * board.getH()) / 6);

		network.addConnection(nodes[3], nodes[4]).setNbResourceNeeded(2);
		network.addConnection(nodes[3], nodes[5]).setNbResourceNeeded(2);
		network.addConnection(nodes[4], nodes[5]).setNbResourceNeeded(2);

		network.addConnection(nodes[0], nodes[3]).setNbResourceNeeded(1);

//		doShuffle(board);
//		doGraphLayout(board);
		
		doPathDuplicate(board);

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

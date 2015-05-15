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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import name.herve.bastod.tools.GameException;
import name.herve.bastod.tools.graph.Dijkstra;
import name.herve.bastod.tools.graph.Graph;
import name.herve.bastod.tools.math.Dimension;
import name.herve.networktycoon.delaunay.Pnt;
import name.herve.networktycoon.delaunay.Triangle;
import name.herve.networktycoon.delaunay.Triangulation;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJointDef;

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
	private final static int MAX_PATH_LENGTH = 6;
	
	public final static float GFX_NODE_RADIUS = 2.5f;
	public final static float GFX_CE_WIDTH = 8f;
	public final static float GFX_CE_HEIGHT = 2f;

	private final static String[] DEFAULT_NODE_NAMES = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private final static String[] DEFAULT_RESOURCE_TYPES = new String[] { "Joker", "Red", "Blue", "Green", "Yellow", "Pink", "Orange", "Cyan", "Magenta", "Black" };
	private final static Color[] DEFAULT_RESOURCE_COLORS = new Color[] { Color.GRAY, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.BLACK };

	private Random rd;

	public BoardFactory() {
		super();
		setSeed(System.currentTimeMillis());
	}

	private Board createBoard() throws GameException {
		return new Board(DEFAULT_BOARD_DIMENSION);
	}

	private void doGraphLayout(Board b) {
		World world = new World(new Vec2(0, 0));
		jbox2dInitWorld(world, b);

		float dt = 1f / 60f;
		int velocityIterations = 6;
		int positionIterations = 2;
		for (int i = 0; i < 300; i++) {
			world.step(dt, velocityIterations, positionIterations);
		}
		
		jbox2dGetLayoutData(b);
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
		for (int i = 0; i < (network.getNbNodes() - 1); i++) {
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

		int limit = (int) ((0.08 * network.getNbNodes() * (network.getNbNodes() - 1)) / 2);
		for (Connection c : network.getConnections()) {
			if (count.get(c) >= limit) {
				c.setExpectedNbPath(2);
			}
		}
	}

	private void doPostInit(Board board, int nbResourceTypes) {
		doPathDuplicate(board);
		doResources(board, nbResourceTypes);
		doGraphLayout(board);
	}

	private void doResources(Board board, int nbResourceTypes) {
		Network network = board.getNetwork();

		for (int r = 0; r < nbResourceTypes; r++) {
			ResourceType resourceType = new ResourceType(DEFAULT_RESOURCE_TYPES[r]);
			resourceType.setColor(DEFAULT_RESOURCE_COLORS[r]);
			if (r == 0) {
				resourceType.setJocker(true);
			}
			board.registerResourceType(resourceType);
		}

		int overallBoardResources = 0;
		for (Connection c : network.getConnections()) {
			overallBoardResources += c.getExpectedNbPath() * c.getNbResourceNeeded();
		}

		List<Connection> sorted = new ArrayList<Connection>();
		sorted.addAll(network.getConnections());
		Collections.sort(sorted, new Comparator<Connection>() {

			@Override
			public int compare(Connection o1, Connection o2) {
				return (o2.getNbResourceNeeded() - o1.getNbResourceNeeded()) + (100 * (o2.getExpectedNbPath() - o1.getExpectedNbPath()));
			}
		});

		int avgResourceNb = (int) Math.ceil(overallBoardResources / (double) nbResourceTypes);
		int[] left = new int[nbResourceTypes];
		Arrays.fill(left, avgResourceNb);
		for (Connection c : sorted) {
			do {
				int r = rd.nextInt(nbResourceTypes);
				ResourceType rt = board.getResourceTypes().get(r);
				if ((left[r] >= c.getNbResourceNeeded()) && !c.hasResourceType(rt)) {
					c.addResourceType(rt);
					left[r] -= c.getNbResourceNeeded();
				}
			} while (c.getExpectedNbPath() != c.getNbPath());
			c.initConnectionElements();
		}
	}

	public Board getEmptyBoard() throws GameException {
		Board board = createBoard();

		return board;
	}

	public Board getRandomBoard() throws GameException {
		Board board = getEmptyBoard();

		int nbResourceTypes = NB_RESOURCES + rd.nextInt(NB_RESOURCES / 2);
		int nbNode = NB_NODES + rd.nextInt(NB_NODES / 2);
		int noCloseNeighbourRange = CLOSE_RANGE;
		int maxNeighbourRange = MAX_RANGE;

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
			Node node = new Node(n, DEFAULT_NODE_NAMES[n]);
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

		for (Connection c : network.getConnections()) {
			c.setNbResourceNeeded((int) Math.max(1, Math.floor((MAX_PATH_LENGTH * c.distance()) / maxDist)));
		}

		doPostInit(board, nbResourceTypes);

		return board;
	}

	public Board getSampleBoard() throws GameException {
		Board board = getEmptyBoard();

		Network network = board.getNetwork();

		Node[] nodes = new Node[6];
		for (int n = 0; n < nodes.length; n++) {
			nodes[n] = new Node(n, DEFAULT_NODE_NAMES[n]);
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

		doPostInit(board, 6);

		return board;
	}

	private Body jbox2dCreateAnchor(World world, float x, float y) {
		FixtureDef nfd = new FixtureDef();
		CircleShape ncd = new CircleShape();
		ncd.m_radius = 0.01f;
		nfd.shape = ncd;

		BodyDef a1 = new BodyDef();
		a1.position.set(new Vec2(x, y));
		a1.type = BodyType.STATIC;
		Body a1b = world.createBody(a1);
		a1b.createFixture(nfd);
		return a1b;
	}

	public void jbox2dGetLayoutData(Board b) {
		for (Node n : b.getNetwork()) {
			Body nbody = (Body) n.getStuff();
			Vec2 pos = nbody.getPosition();
			n.setX(Math.round(pos.x));
			n.setY(Math.round(pos.y));
		}

		for (Connection c : b.getNetwork().getConnections()) {
			for (ResourceType rt : c.getResourceTypes()) {
				for (ConnectionElement ce : c.getConnectionElements(rt)) {
					Body cbody = (Body) ce.getStuff();
					Vec2 pos = cbody.getPosition();
					ce.setX(pos.x);
					ce.setY(pos.y);
					ce.setO(cbody.getAngle());
				}
			}
		}
	}

	public void jbox2dInitWorld(World world, Board b) {
		world.setGravity(new Vec2(0, 0));

		FixtureDef nfd = new FixtureDef();
		CircleShape ncd = new CircleShape();
		ncd.m_radius = GFX_NODE_RADIUS;
		nfd.shape = ncd;
		nfd.density = 10f;

		float bxw = GFX_CE_WIDTH / 2f;
		float bxh = GFX_CE_HEIGHT / 2f;

		FixtureDef box1 = new FixtureDef();
		PolygonShape b1 = new PolygonShape();
		b1.setAsBox(bxw, bxh);
		box1.shape = b1;
		box1.density = 10.0f;

		float anchorLength = 5f;

		for (Node n : b.getNetwork()) {
			BodyDef nbd = new BodyDef();
			nbd.position.set(new Vec2(n.getX(), n.getY()));
			nbd.type = BodyType.DYNAMIC;
			Body nbody = world.createBody(nbd);
			nbody.createFixture(nfd);
			n.setStuff(nbody);

			jbox2dLink(world, jbox2dCreateAnchor(world, n.getX() - anchorLength, n.getY() - anchorLength), nbody);
			jbox2dLink(world, jbox2dCreateAnchor(world, n.getX() - anchorLength, n.getY() + anchorLength), nbody);
			jbox2dLink(world, jbox2dCreateAnchor(world, n.getX() + anchorLength, n.getY() - anchorLength), nbody);
			jbox2dLink(world, jbox2dCreateAnchor(world, n.getX() + anchorLength, n.getY() + anchorLength), nbody);
		}

		float hbxw = bxw / 2;

		Vec2 delta = new Vec2(0.01f, 0.01f);

		for (Connection c : b.getNetwork().getConnections()) {
			Node n1 = c.getNode1();
			Node n2 = c.getNode2();

			Vec2 start = new Vec2((n1.getX()), (n1.getY()));
			Vec2 end = new Vec2((n2.getX()), (n2.getY()));
			Vec2 step = end.sub(start);
			float angle = (float) (Math.atan2(step.y, step.x));
			step.mulLocal(1.0f / (c.getNbResourceNeeded() + 1));
			float length = 1f * (step.length() - bxw);

			Body[] other = new Body[c.getNbResourceNeeded()];
			boolean firstPath = true;
			for (ResourceType rt : c.getResourceTypes()) {
				Vec2 pos = start.add(step);
				Body previous = (Body) n1.getStuff();

				List<ConnectionElement> ce = c.getConnectionElements(rt);

				for (int i = 0; i < ce.size(); i++) {
					BodyDef nbd = new BodyDef();
					nbd.position.set(firstPath ? pos : pos.add(delta));
					nbd.angle = angle;
					nbd.type = BodyType.DYNAMIC;
					Body nbody = world.createBody(nbd);
					nbody.createFixture(box1);
					ce.get(i).setStuff(nbody);

					pos.addLocal(step);

					if (firstPath) {
						other[i] = nbody;
					} else {
						float mid = (ce.size() - 1) / 2f;
						float small = 1.0f + (0.2f * ((1 + mid) - Math.abs(i - mid)));

						jbox2dLink(world, bxw * small, other[i], hbxw, 0f, nbody, hbxw, 0f);
						jbox2dLink(world, bxw * small, other[i], -hbxw, 0f, nbody, -hbxw, 0f);
					}

					if (i == 0) {
						jbox2dLink(world, length, previous, nbody, -hbxw, 0f);
					} else {
						jbox2dLink(world, length, previous, hbxw, 0f, nbody, -hbxw, 0f);
					}
					previous = nbody;
				}

				jbox2dLink(world, length, previous, hbxw, 0f, (Body) n2.getStuff());

				firstPath = false;
			}
		}
	}

	private void jbox2dLink(World world, Body b1, Body b2) {
		float length = b1.getPosition().sub(b2.getPosition()).length();

		DistanceJointDef jd = new DistanceJointDef();
		jd.collideConnected = false;

		jd.bodyA = b1;
		jd.bodyB = b2;

		jd.length = length;
		jd.frequencyHz = 5f;
		jd.dampingRatio = 0.01f;
		world.createJoint(jd);
	}

	private void jbox2dLink(World world, float length, Body b1, Body b2, float x2, float y2) {
		jbox2dLink(world, length, b1, 0, 0, b2, x2, y2);
	}

	private void jbox2dLink(World world, float length, Body b1, float x1, float y1, Body b2) {
		jbox2dLink(world, length, b1, x1, y1, b2, 0, 0);
	}

	private void jbox2dLink(World world, float length, Body b1, float x1, float y1, Body b2, float x2, float y2) {
		DistanceJointDef jd = new DistanceJointDef();
		jd.collideConnected = false;

		jd.bodyA = b1;
		jd.bodyB = b2;
		jd.localAnchorA.set(x1, y1);
		jd.localAnchorB.set(x2, y2);

		jd.length = length;
		jd.frequencyHz = 5f;
		jd.dampingRatio = 10f;
		world.createJoint(jd);
	}

	public void setSeed(long seed) {
		rd = new Random(seed);
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

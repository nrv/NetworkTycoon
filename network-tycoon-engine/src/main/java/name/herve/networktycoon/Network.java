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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import name.herve.bastod.tools.graph.Dijkstra;
import name.herve.bastod.tools.graph.Graph;
import name.herve.bastod.tools.graph.Node;

/**
 * @author Nicolas HERVE
 */
public class Network implements Iterable<EndPoint> {
	private List<EndPoint> endpoints;
	private List<Connection> connections;
	private Graph graph;
	private Map<EndPoint, Node> graphMap1;
	private Map<Node, EndPoint> graphMap2;
	private Dijkstra graphDijkstra;

	public Network() {
		super();
		endpoints = new ArrayList<EndPoint>();
		connections = new ArrayList<Connection>();
	}

	public Connection addConnection(EndPoint n1, EndPoint n2) {
		Connection c = new Connection();
		c.setEndPoint1(n1);
		c.setEndPoint2(n2);
		n1.addConnection(c, n2);
		n2.addConnection(c, n1);
		connections.add(c);
		return c;
	}

	public Network addEndPoint(EndPoint e) {
		endpoints.add(e);
		return this;
	}

	public void buildInternalGraph() {
		graph = new Graph();
		graphMap1 = new HashMap<EndPoint, Node>();
		graphMap2 = new HashMap<Node, EndPoint>();

		for (EndPoint n : endpoints) {
			Node nn = new Node();
			graphMap1.put(n, nn);
			graphMap2.put(nn, n);
			graph.addNode(nn);
		}
		for (Connection c : connections) {
			graph.addEdge(graphMap1.get(c.getEndPoint1()), graphMap1.get(c.getEndPoint2()), c.getNbResourceNeeded());
		}
		graphDijkstra = new Dijkstra(graph);
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public List<EndPoint> getEndPoints() {
		return endpoints;
	}

	public int getNbConnections() {
		return connections.size();
	}

	public int getNbEndPoints() {
		return endpoints.size();
	}

	public List<EndPoint> getShortestPath(EndPoint start, EndPoint end) {
		List<Node> nPath = graphDijkstra.getPathNodes(graphMap1.get(start), graphMap1.get(end));
		List<EndPoint> epPath = new ArrayList<EndPoint>();

		for (Node n : nPath) {
			epPath.add(graphMap2.get(n));
		}

		return epPath;
	}

	@Override
	public Iterator<EndPoint> iterator() {
		return endpoints.iterator();
	}

	public void removeConnection(Connection c) {
		c.getEndPoint1().removeConnection(c);
		c.getEndPoint2().removeConnection(c);
		connections.remove(c);
	}

	public void removeNode(EndPoint n) {
		List<Connection> toRemove = new ArrayList<Connection>();
		for (Connection c : n) {
			toRemove.add(c);
		}
		for (Connection c : toRemove) {
			removeConnection(c);
		}
		endpoints.remove(n);
	}
}

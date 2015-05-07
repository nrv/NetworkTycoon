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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Nicolas HERVE
 */
public class Network implements Iterable<Node> {
	private Set<Node> nodes;
	private List<Connection> connections;

	public Network() {
		super();
		nodes = new TreeSet<Node>();
		connections = new ArrayList<Connection>();
	}

	public Connection addConnection(Node n1, Node n2) {
		Connection c = new Connection(n1, n2);
		n1.addConnection(c, n2);
		n2.addConnection(c, n1);
		connections.add(c);
		return c;
	}

	public Network addNode(Node e) {
		nodes.add(e);
		return this;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public int getNbConnections() {
		return connections.size();
	}

	public int getNbNodes() {
		return nodes.size();
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	public void removeConnection(Connection c) {
		for (Node n : c) {
			n.removeConnection(c);
		}
		connections.remove(c);
	}

	public void removeNode(Node n) {
		List<Connection> toRemove = new ArrayList<Connection>();
		for (Connection c : n) {
			toRemove.add(c);
		}
		for (Connection c : toRemove) {
			removeConnection(c);
		}
		nodes.remove(n);
	}
}

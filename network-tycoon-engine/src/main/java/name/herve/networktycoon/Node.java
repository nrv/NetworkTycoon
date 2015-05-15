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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Nicolas HERVE
 */
public class Node implements Comparable<Node>, Iterable<Connection> {
	private int id;
	private String name;
	private float x;
	private float y;
	private Map<Connection, Node> connections;
	private Object stuff;

	public Node(int id, String name) {
		super();

		this.name = name;
		this.id = id;
		connections = new HashMap<Connection, Node>();
	}

	public void addConnection(Connection c, Node n) {
		connections.put(c, n);
	}

	@Override
	public int compareTo(Node o) {
		return id - o.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Node other = (Node) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public Connection getConnectionTo(Node other) {
		for (Entry<Connection, Node> e : connections.entrySet()) {
			if (e.getValue().equals(other)) {
				return e.getKey();
			}
		}

		return null;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getNbConnections() {
		return connections.size();
	}

	public Object getStuff() {
		return stuff;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + id;
		return result;
	}

	public boolean isConnectedTo(Node other) {
		return getConnectionTo(other) != null;
	}

	@Override
	public Iterator<Connection> iterator() {
		return connections.keySet().iterator();
	}

	public void removeConnection(Connection c) {
		connections.remove(c);
	}

	public void setStuff(Object stuff) {
		this.stuff = stuff;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", name=" + name + "]";
	}
}

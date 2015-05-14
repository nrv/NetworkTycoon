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
import java.util.Set;

/**
 * @author Nicolas HERVE
 */
public class Connection implements Iterable<Node> {
	private List<Node> nodes;
	private Map<ResourceType, List<ConnectionElement>> path;
	private int nbResourceNeeded;
	private int expectedNbPath;

	public Connection() {
		super();
		nodes = new ArrayList<Node>();
		path = new HashMap<ResourceType, List<ConnectionElement>>();
	}

	public Connection(Node n1, Node n2) {
		this();
		add(n1);
		add(n2);
		setExpectedNbPath(1);
	}

	public Connection add(Node e) {
		nodes.add(e);
		return this;
	}

	public Connection addResourceType(ResourceType resourceType) {
		path.put(resourceType, new ArrayList<ConnectionElement>());
		return this;
	}

	public double distance() {
		return Math.sqrt(Math.pow(nodes.get(0).getX() - nodes.get(1).getX(), 2) + Math.pow(nodes.get(0).getY() - nodes.get(1).getY(), 2));
	}

	public int getExpectedNbPath() {
		return expectedNbPath;
	}

	public int getNbPath() {
		return path.size();
	}

	public int getNbResourceNeeded() {
		return nbResourceNeeded;
	}

	public Node getNode1() {
		return nodes.get(0);
	}

	public Node getNode2() {
		return nodes.get(1);
	}

	public Set<ResourceType> getResourceTypes() {
		return path.keySet();
	}

	public boolean hasResourceType(ResourceType resourceType) {
		return path.containsKey(resourceType);
	}

	// public void setNbPath(int nbPath) {
	// this.nbPath = nbPath;
	// }

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	public void setExpectedNbPath(int expectedNbPath) {
		this.expectedNbPath = expectedNbPath;
	}

	public void setNbResourceNeeded(int nbResourceNeeded) {
		this.nbResourceNeeded = nbResourceNeeded;
	}

	@Override
	public String toString() {
		return "Connection [" + getNode1().getId() + "->" + getNode2().getId() + ", nbResourceNeeded=" + getNbResourceNeeded() + ", nbPath=" + getNbPath() + "]";
	}
}

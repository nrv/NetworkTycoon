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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Nicolas HERVE
 */
public class Connection implements Iterable<Node> {
	private Set<Node> nodes;
	private ResourceType resourceType;

	public Connection() {
		super();
		nodes = new TreeSet<Node>();
	}

	public Connection(Node n1, Node n2) {
		this();
		add(n1);
		add(n2);
	}

	public Connection add(Node e) {
		nodes.add(e);
		return this;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}

	public Connection setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
		return this;
	}
}

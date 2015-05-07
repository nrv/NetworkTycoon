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
public class Network implements Iterable<Node> {
	private Set<Node> nodes;

	public Network() {
		super();
		nodes = new TreeSet<Node>();
	}

	public void addNode(Node e) {
		nodes.add(e);
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}
}

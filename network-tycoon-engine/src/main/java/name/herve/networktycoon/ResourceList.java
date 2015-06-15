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

import name.herve.bastod.tools.GameException;

/**
 * @author Nicolas HERVE
 */
public abstract class ResourceList {
	private int size;

	public ResourceList() {
		super();
		size = 0;
	}

	protected abstract void add(Resource r) throws GameException;

	public void addResource(Resource r) throws GameException {
		add(r);
		size++;
	}

	public abstract Resource getResource(ResourceType t) throws GameException;

	public abstract boolean hasResource(ResourceType t);

	public boolean isEmpty() {
		return size() == 0;
	}

	protected abstract Resource remove(Resource r) throws GameException;

	public Resource removeResource(Resource r) throws GameException {
		Resource rm = remove(r);
		size--;
		return rm;
	}

	public Resource removeResource(ResourceType t) throws GameException {
		return removeResource(getResource(t));
	}

	public int size() {
		return size;
	}
}

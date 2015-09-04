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
public class ResourceListFixedSize extends ResourceList implements NetworkSerializable {
	private Resource[] resources;

	public ResourceListFixedSize(int size) {
		super();
		resources = new Resource[size];
	}

	@Override
	protected void add(Resource r) throws GameException {
		if (isFull()) {
			throw new GameException("ResourceListFixedSize full");
		}

		for (int i = 0; i < resources.length; i++) {
			if (resources[i] == null) {
				resources[i] = r;
				return;
			}
		}
	}

	public Resource drawResource() throws GameException {
		Resource r = null;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i] != null) {
				r = resources[i];
				removeResource(r);
				return r;
			}
		}
		throw new GameException("Unable to draw a resource");
	}

	public int getMaxSize() {
		return resources.length;
	}

	public int getNbJocker() {
		int nbj = 0;
		for (int i = 0; i < resources.length; i++) {
			if ((resources[i] != null) && (resources[i].getType().isJocker())) {
				nbj++;
			}
		}
		return nbj;
	}

	@Override
	public Resource getResource(ResourceType t) throws GameException {
		for (int i = 0; i < resources.length; i++) {
			if ((resources[i] != null) && (resources[i].getType().equals(t))) {
				return resources[i];
			}
		}
		throw new GameException("Unable to get a resource of type " + t);
	}

	@Override
	public boolean hasResource(ResourceType t) {
		for (int i = 0; i < resources.length; i++) {
			if ((resources[i] != null) && (resources[i].getType().equals(t))) {
				return true;
			}
		}
		return false;
	}

	public boolean isFull() {
		return size() >= getMaxSize();
	}

	@Override
	protected Resource remove(Resource r) throws GameException {
		for (int i = 0; i < resources.length; i++) {
			if (resources[i] == r) {
				resources[i] = null;
				return r;
			}
		}
		throw new GameException("Unable to remove a resource " + r);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(resources[0] == null ? " " : resources[0].getType().getCode());
		for (int i = 1; i < resources.length; i++) {
			sb.append(", ");
			sb.append(resources[i] == null ? " " : resources[i].getType().getCode());
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String serialize() {
		// TODO Auto-generated method stub
		return null;
	}
}

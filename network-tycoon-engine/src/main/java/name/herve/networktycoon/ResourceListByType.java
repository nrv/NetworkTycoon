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
import java.util.List;
import java.util.Map;

import name.herve.bastod.tools.GameException;

/**
 * @author Nicolas HERVE
 */
public class ResourceListByType extends ResourceList {
	private Map<ResourceType, List<Resource>> resources;

	public ResourceListByType() {
		super();
		resources = new HashMap<ResourceType, List<Resource>>();
	}

	@Override
	public void add(Resource r) throws GameException {
		if (!resources.containsKey(r.getType())) {
			resources.put(r.getType(), new ArrayList<Resource>());
		}
		resources.get(r.getType()).add(r);
	}

	public int getNbResource(ResourceType t) {
		if (!resources.containsKey(t)) {
			return 0;
		}

		return resources.get(t).size();
	}

	@Override
	public Resource getResource(ResourceType t) throws GameException {
		if (!hasResource(t)) {
			throw new GameException("Unable to get a resource of type " + t);
		}
		return resources.get(t).get(0);
	}

	@Override
	public boolean hasResource(ResourceType t) {
		return getNbResource(t) > 0;
	}

	@Override
	protected Resource remove(Resource r) throws GameException {
		if (!resources.containsKey(r.getType())) {
			throw new GameException("Unable to remove a resource " + r);
		}
		if (!resources.get(r.getType()).contains(r)) {
			throw new GameException("Unable to remove a resource " + r);
		}
		if (resources.get(r.getType()).remove(r)) {
			return r;
		}
		throw new GameException("Unable to remove a resource " + r);
	}
}

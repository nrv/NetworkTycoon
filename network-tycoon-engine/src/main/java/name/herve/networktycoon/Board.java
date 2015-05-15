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

import name.herve.bastod.tools.math.Dimension;

/**
 * @author Nicolas HERVE
 */
public class Board {
	private Dimension dimension;
	private Map<ResourceType, Integer> resourceTypes;
	private List<ResourceType> indexedResourceTypes;
	private Network network;

	public Board(Dimension dimension) {
		super();

		this.dimension = dimension;
		indexedResourceTypes = new ArrayList<ResourceType>();
		network = new Network();
	}

	public void countResourceTypes() {
		resourceTypes = new HashMap<ResourceType, Integer>();
		for (ResourceType rt : indexedResourceTypes) {
			resourceTypes.put(rt, 0);
		}
		for (Connection c : network.getConnections()) {
			for (ResourceType rt : c.getResourceTypes()) {
				resourceTypes.put(rt, resourceTypes.get(rt) + c.getNbResourceNeeded());
			}
		}
	}

	public Dimension getDimension() {
		return dimension;
	}

	public int getH() {
		return dimension.getH();
	}

	public Network getNetwork() {
		return network;
	}

	public ResourceType getResourceType(int idx) {
		return indexedResourceTypes.get(idx);
	}

	public List<ResourceType> getResourceTypes() {
		return indexedResourceTypes;
	}

	public int getTotalNbResourceType(ResourceType rt) {
		return resourceTypes.get(rt);
	}

	public int getW() {
		return dimension.getW();
	}

	public void registerResourceType(ResourceType r) {
		indexedResourceTypes.add(r);
	}
}

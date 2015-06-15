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
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Nicolas HERVE
 */
public class Connection extends TwoEndPoints {
	private Map<ResourceType, List<ConnectionElement>> path;
	private int nbResourceNeeded;
	private int expectedNbPath;

	public Connection() {
		super();
		path = new HashMap<ResourceType, List<ConnectionElement>>();
		setExpectedNbPath(1);
	}

	public Connection addResourceType(ResourceType resourceType) {
		path.put(resourceType, new ArrayList<ConnectionElement>());
		return this;
	}

	public List<ConnectionElement> getConnectionElements(ResourceType rt) {
		return path.get(rt);
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

	public Set<ResourceType> getResourceTypes() {
		return path.keySet();
	}

	public boolean hasResourceType(ResourceType resourceType) {
		return path.containsKey(resourceType);
	}

	public void initConnectionElements() {
		for (Entry<ResourceType, List<ConnectionElement>> e : path.entrySet()) {
			e.getValue().clear();
			for (int i = 0; i < nbResourceNeeded; i++) {
				e.getValue().add(new ConnectionElement());
			}
		}
	}

	public void setExpectedNbPath(int expectedNbPath) {
		this.expectedNbPath = expectedNbPath;
	}

	public void setNbResourceNeeded(int nbResourceNeeded) {
		this.nbResourceNeeded = nbResourceNeeded;
	}

	@Override
	public String toString() {
		return "Connection [" + getEndPoint1().getId() + "->" + getEndPoint2().getId() + ", nbResourceNeeded=" + getNbResourceNeeded() + ", nbPath=" + getNbPath() + "]";
	}
}

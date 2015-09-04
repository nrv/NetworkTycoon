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
package name.herve.networktycoon.model;

import java.awt.Color;
import name.herve.networktycoon.Player;
import name.herve.networktycoon.ResourceListByType;

/**
 * @author Nicolas HERVE
 */
public class ClientPlayer implements Player {
	private String name;
	private Color color;
	private ResourceListByType resources;

	public ClientPlayer() {
		super();
		this.resources = new ResourceListByType();
	}

	public Color getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	@Override
	public String serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setName(String name) {
		this.name = name;
	}
}

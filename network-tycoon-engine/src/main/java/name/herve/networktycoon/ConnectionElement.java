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

/**
 * @author Nicolas HERVE
 */
public class ConnectionElement {
	private float x;
	private float y;
	private float o;

	public ConnectionElement() {
		super();
	}

	public ConnectionElement(float x, float y, float o) {
		super();
		this.x = x;
		this.y = y;
		this.o = o;
	}

	public float getO() {
		return o;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setO(float o) {
		this.o = o;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
}

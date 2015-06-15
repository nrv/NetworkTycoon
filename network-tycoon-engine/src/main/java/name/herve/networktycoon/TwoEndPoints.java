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
public abstract class TwoEndPoints {
	private EndPoint ep1;
	private EndPoint ep2;

	public TwoEndPoints() {
		super();
	}

	public TwoEndPoints(EndPoint ep1, EndPoint ep2) {
		super();
		this.ep1 = ep1;
		this.ep2 = ep2;
	}

	public EndPoint getEndPoint1() {
		return ep1;
	}

	public EndPoint getEndPoint2() {
		return ep2;
	}

	public void setEndPoint1(EndPoint ep1) {
		this.ep1 = ep1;
	}

	public void setEndPoint2(EndPoint ep2) {
		this.ep2 = ep2;
	}
	
	public double getBoardDistance() {
		return Math.sqrt(Math.pow(ep1.getX() - ep2.getX(), 2) + Math.pow(ep1.getY() - ep2.getY(), 2));
	}
}

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
import name.herve.networktycoon.engine.Engine;


/**
 * @author Nicolas HERVE
 */
public class TestTextGame {
	

	public static void main(String[] args) {
		try {
			BoardFactory bf = new BoardFactory();
			bf.setSeed(7000);
			GameFactory gf = new GameFactory();
			Game g = gf.createGame(bf.getRandomBoard(), 2);
			
			for (Player p : g.getPlayers()) {
				p.
			}
			
			Engine engine = new Engine();
			engine.init(g);
			engine.loop();
			
		} catch (GameException e) {
			e.printStackTrace();
		}
	}
}

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

import java.awt.Color;
import java.util.List;

import name.herve.networktycoon.model.EnginePlayer;


/**
 * @author Nicolas HERVE
 */
public class GameFactory {
	private final static Color[] DEFAULT_PLAYER_COLORS = new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE};

	public GameFactory() {
		super();
	}
	
	public Game createGame(Board board, int nbPlayers) {
		Game game = new Game(board);
		
		for (int i = 0; i < nbPlayers; i++) {
			EnginePlayer p = new EnginePlayer();
			p.setName("Player " + i);
			p.setColor(DEFAULT_PLAYER_COLORS[i]);
			game.addPlayer(p);
		}
		
		createResources(game);
		createGoals(game);
		
		return game;
	}
	
	private void createResources(Game game) {
		for (ResourceType rt : game.getBoard().getResourceTypes()) {
			int nb = (int)(2 * game.getBoard().getTotalNbResourceType(rt));
			for (int n = 0; n < nb; n++) {
				Resource r = new Resource(rt);
				game.addResource(r);
			}
		}
	}
	
	private void createGoals(Game game) {
		List<EndPoint> eps = game.getBoard().getEndPoints();
		for (int i = 0; i < eps.size() - 1; i++) {
			for (int j = i + 1; j < eps.size(); j++) {
				Goal g = new Goal(eps.get(i), eps.get(j));
				int nbr = game.getBoard().getNbResourceNeeded(g);
				if (nbr > 4) {
					g.setNbPoints(nbr * 2);
					game.addGoal(g);
				}
			}
		}
	}
}

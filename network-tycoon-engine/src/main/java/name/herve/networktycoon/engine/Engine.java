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
package name.herve.networktycoon.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.herve.bastod.tools.GameException;
import name.herve.networktycoon.Game;
import name.herve.networktycoon.Goal;
import name.herve.networktycoon.Player;
import name.herve.networktycoon.ResourceListFixedSize;

/**
 * @author Nicolas HERVE
 */
public class Engine {
	private Game game;
	private BoardInterface bi;
	private Map<Player, PlayerInterface> pis;
	private List<Player> orderedPlayers;
	private int currentPlayer;

	public Engine() {
		super();
	}

	public void start(Game game, BoardInterface bi) throws GameException {
		this.game = game;
		this.bi = bi;
		bi.setBoard(game.getBoard());

		fillShownResources();
		
		pis = new HashMap<Player, PlayerInterface>();

		orderedPlayers = new ArrayList<Player>();
		currentPlayer = -1;
		
		for (Player p : game.getPlayers()) {
			PlayerInterface pi = bi.getPlayerInterface(p);
			pis.put(p, pi);
			pi.welcomePlayer();
			orderedPlayers.add(p);
			List<Goal> potentialGoals = new ArrayList<Goal>();
			for (int i = 0; i < 3; i++) {
				potentialGoals.add(game.drawGoal());
			}
			List<Goal> keptGoals = pi.chooseGoalsToKeep(potentialGoals, 2);
		}
		
		Collections.shuffle(orderedPlayers);
	}
	
	public Player getNextPlayer() {
		currentPlayer++;
		
		if (currentPlayer >= orderedPlayers.size()) {
			currentPlayer = 0;
		}
		
		return orderedPlayers.get(currentPlayer);
	}

	private void fillShownResources() throws GameException {
		ResourceListFixedSize rs = game.getShownResources();
		while (!rs.isFull()) {
			while (!rs.isFull()) {
				drawOneResourceToShown();
			}
			if (rs.getNbJocker() >= 3) {
				bi.warningTooManyJockersInShownResources();
				game.returnAllShownResourcesToDeck();
			}
		}
	}

	public void drawOneResourceToShown() throws GameException {
		game.drawOneResourceToShown();
		bi.updateShownResources(game.getShownResources());
	}

}

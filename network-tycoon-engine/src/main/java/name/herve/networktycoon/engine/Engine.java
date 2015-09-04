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
import java.util.List;

import name.herve.bastod.tools.GameException;
import name.herve.networktycoon.Game;
import name.herve.networktycoon.Goal;
import name.herve.networktycoon.Player;
import name.herve.networktycoon.ResourceListFixedSize;
import name.herve.networktycoon.model.EnginePlayer;

/**
 * @author Nicolas HERVE
 */
public class Engine implements GameEventListener {
	private Game game;
	private List<EnginePlayer> orderedPlayers;
	private int currentPlayer;

	public Engine() {
		super();
	}

	public void eventDispatch(GameEvent event) {
		for (EnginePlayer p : orderedPlayers) {
			eventSend(event, p);
		}
	}

	public void eventDispatchExcept(GameEvent event, EnginePlayer exception) {
		for (EnginePlayer p : orderedPlayers) {
			if (p != exception) {
				eventSend(event, p);
			}
		}
	}

	public void eventSend(GameEvent event, EnginePlayer p) {
		PlayerInterface pi = p.getPlayerInterface();
		if (pi != null) {
			pi.processGameEvent(event);
		}
	}

	private void fillShownResources() throws GameException {
		ResourceListFixedSize rs = game.getShownResources();
		while (!rs.isFull()) {
			while (!rs.isFull()) {
				game.drawOneResourceToShown();
			}
			if (rs.getNbJocker() >= 3) {
				game.returnAllShownResourcesToDeck();
				eventDispatch(GameEvent.message("Too many jockers, changing resources deck"));
			}
		}
		eventDispatch(GameEvent.resourceDeckChanged(game.getShownResources()));
	}

	private boolean gameEnded() {
		return false;
	}

	private Player getNextPlayer() {
		currentPlayer++;

		if (currentPlayer >= orderedPlayers.size()) {
			currentPlayer = 0;
		}

		return orderedPlayers.get(currentPlayer);
	}

	public void init(Game game) throws GameException {
		this.game = game;

		fillShownResources();

		orderedPlayers = new ArrayList<EnginePlayer>();
		currentPlayer = -1;

		for (EnginePlayer p : game.getPlayers()) {
			eventSend(GameEvent.message("Welcome " + p.getName()), p);
			orderedPlayers.add(p);

			List<Goal> potentialGoals = new ArrayList<Goal>();
			for (int i = 0; i < 3; i++) {
				potentialGoals.add(game.drawGoal());
			}
			eventSend(GameEvent.chooseGoals(potentialGoals, 2), p);
		}

		Collections.shuffle(orderedPlayers);
	}

	public void loop() throws GameException {
		while (!gameEnded()) {
			playerTurn(getNextPlayer());
		}
	}

	private void playerTurn(Player p) throws GameException {
		eventDispatch(GameEvent.playerTurn(p));
	}

	@Override
	public void processGameEvent(GameEvent e) {
		// TODO Auto-generated method stub

	}

}

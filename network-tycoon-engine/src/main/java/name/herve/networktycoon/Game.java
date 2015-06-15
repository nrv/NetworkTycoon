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
import java.util.List;

import name.herve.bastod.tools.GameException;

/**
 * @author Nicolas HERVE
 */
public class Game {
	private Board board;
	private List<Player> players;
	private CardDeck<Resource> resourcesDeck;
	private CardDeck<Goal> goalsDeck;
	private ResourceListFixedSize shownResources;

	public Game(Board board) {
		super();
		this.board = board;

		players = new ArrayList<Player>();
		resourcesDeck = new CardDeck<Resource>();
		goalsDeck = new CardDeck<Goal>();
		shownResources = new ResourceListFixedSize(5);
	}

	public void addGoal(Goal e) {
		goalsDeck.discard(e);
	}

	public boolean addPlayer(Player e) {
		return players.add(e);
	}

	public void addResource(Resource e) {
		resourcesDeck.discard(e);
	}

	public Goal drawGoal() {
		return goalsDeck.draw();
	}

	public void drawOneResourceToShown() throws GameException {
		if (shownResources.isFull()) {
			throw new GameException("ShownResources is full");
		}

		shownResources.addResource(resourcesDeck.draw());
	}

	public Board getBoard() {
		return board;
	}

	public int getNbPlayers() {
		return players.size();
	}

	public List<Player> getPlayers() {
		return players;
	}

	public ResourceListFixedSize getShownResources() {
		return shownResources;
	}

	public void returnAllShownResourcesToDeck() throws GameException {
		while (!shownResources.isEmpty()) {
			resourcesDeck.discard(shownResources.drawResource());
		}
	}
}

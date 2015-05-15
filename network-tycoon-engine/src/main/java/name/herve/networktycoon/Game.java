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
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Nicolas HERVE
 */
public class Game {
	private Board board;
	private List<Player> players;
	private int turn;
	private Deque<Resource> deck;
	private List<Resource> discarded;
	private List<Resource> shown;

	public Game(Board board) {
		super();
		this.board = board;
		turn = 0;
		
		players = new ArrayList<Player>();
		deck = new LinkedList<Resource>();
		discarded = new ArrayList<Resource>();
		shown = new ArrayList<Resource>();
	}

	public boolean addPlayer(Player e) {
		return players.add(e);
	}

	public void addToDiscarded(Resource e) {
		discarded.add(e);
	}

	public void addToShown(Resource e) {
		shown.add(e);
	}

	public Board getBoard() {
		return board;
	}
	
	public Resource drawFromDeck() {
		if (deck.isEmpty()) {
			shuffleDiscardedAndAddToDeck();
		}
		return deck.remove();
	}

	public int getNbPlayers() {
		return players.size();
	}

	public void shuffleDiscardedAndAddToDeck() {
		Collections.shuffle(discarded);
		deck.addAll(discarded);
		discarded.clear();
	}
}

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
public class CardDeck<T extends Card> {
	private Deque<T> deck;
	private List<T> discarded;

	public CardDeck() {
		super();
		deck = new LinkedList<T>();
		discarded = new ArrayList<T>();
	}

	public void discard(T e) {
		discarded.add(e);
	}

	public T draw() {
		if (deck.isEmpty()) {
			shuffleDiscardedAndAddToDeck();
		}
		return deck.remove();
	}

	public void shuffleDiscardedAndAddToDeck() {
		Collections.shuffle(discarded);
		deck.addAll(discarded);
		discarded.clear();
	}
}

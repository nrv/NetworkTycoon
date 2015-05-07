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

import java.util.Arrays;
import java.util.Random;

import name.herve.bastod.tools.GameException;
import name.herve.bastod.tools.math.Dimension;

/**
 * @author Nicolas HERVE
 */
public class BoardFactory {
	private final static Dimension DEFAULT_BOARD_DIMENSION = new Dimension(20, 15);
	private final static String[] DEFAULT_RESOURCE_TYPES = new String[]{"*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"};
	private final static String[] DEFAULT_NODES = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
	
	private long seed;

	public BoardFactory() {
		super();
		setSeed(System.currentTimeMillis());
	}

	private Board createBoard() throws GameException {
		return new Board(DEFAULT_BOARD_DIMENSION);
	}

	public Board getEmptyBoard() throws GameException {
		Board board = createBoard();

		return board;
	}

	public Board getRandomBoard() throws GameException {
		Board board = getEmptyBoard();

		Random rd = new Random(seed);
		
		int nbResourceTypes = 4 + rd.nextInt(3);
		int nbNode = 15 + rd.nextInt(11);
		
		// Resources
		for (int r = 0; r < nbResourceTypes; r++) {
			ResourceType resourceType = new ResourceType(DEFAULT_RESOURCE_TYPES[r]);
			if (r == 0) {
				resourceType.setJocker(true);
			}
			board.registerResourceType(resourceType);
		}
		
		
		// Nodes
		Network network = board.getNetwork();
		boolean[][] occupied = new boolean[board.getW()][board.getH()];
		for (int x = 0; x < board.getW(); x++) {
			Arrays.fill(occupied[x], false);
		}
		for (int n = 0; n < nbNode; n++) {
			Node node = new Node(n, DEFAULT_NODES[n]);
			int x, y;
			do {
				x = rd.nextInt(board.getW());
				y = rd.nextInt(board.getH());
			} while (occupied[x][y]);
			occupied[x][y] = true;
			node.setX(x);
			node.setY(y);
			network.addNode(node);
		}
		
		// Connections
		
		return board;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}
}

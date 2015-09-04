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

import java.util.List;

import name.herve.networktycoon.Goal;
import name.herve.networktycoon.Player;

/**
 * @author Nicolas HERVE
 */
public abstract class PlayerInterface implements GameEventListener {
	private Player player;

	public PlayerInterface(Player player) {
		super();
		this.player = player;
	}

	public abstract List<Goal> actionChooseGoalsToKeep(List<Goal> goals, int min);

	public Player getPlayer() {
		return player;
	}

	public abstract void displayMessage(String msg);

	@Override
	public void processGameEvent(GameEvent e) {
		switch (e.getType()) {
		case SRV_MESSAGE:
			displayMessage((String)e.getObject());
			break;
		case SRV_PLAYER_TURN:
			Player p = (Player) e.getObject();
			break;
		}
	}
}

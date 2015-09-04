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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.herve.networktycoon.Goal;
import name.herve.networktycoon.NetworkSerializable;
import name.herve.networktycoon.Player;
import name.herve.networktycoon.ResourceListFixedSize;

/**
 * @author Nicolas HERVE
 */
public class GameEvent implements NetworkSerializable {
	public enum GameEventType {
		SRV_RESOURCE_DECK_CHANGED, SRV_MESSAGE, SRV_CHOOSE_GOALS, SRV_PLAYER_TURN, PL_GOALS_CHOOSEN
	}

	public final static String MAIN = "main";
	public final static String NB = "nb";
	public final static String GOAL = "goal";

	public static GameEvent chooseGoals(List<Goal> goals, int nb) {
		GameEvent ge = new GameEvent(GameEventType.SRV_CHOOSE_GOALS);
		ge.param(NB, Integer.toString(nb));
		int cnt = 0;
		for (Goal g : goals) {
			ge.param(GOAL + cnt, g.serialize());
			cnt++;
		}
		return ge;
	}

	public static GameEvent message(String msg) {
		return new GameEvent(GameEventType.SRV_MESSAGE, msg);
	}

	public static GameEvent playerTurn(Player p) {
		return new GameEvent(GameEventType.SRV_PLAYER_TURN, p.serialize());
	}

	public static GameEvent resourceDeckChanged(ResourceListFixedSize list) {
		return new GameEvent(GameEventType.SRV_RESOURCE_DECK_CHANGED, list.serialize());
	}

	private GameEventType type;
	private Map<String, String> objects;

	public GameEvent(GameEventType type) {
		super();
		this.type = type;
		this.objects = new HashMap<String, String>();
	}

	public GameEvent(GameEventType type, String object) {
		this(type);
		this.type = type;
		param(MAIN, object);
	}

	public Object getObject() {
		return param(MAIN);
	}

	public GameEventType getType() {
		return type;
	}

	public Object param(String key) {
		return objects.get(key);
	}

	public GameEvent param(String key, String value) {
		objects.put(key, value);
		return this;
	}

	@Override
	public String serialize() {
		return null;
	}
}

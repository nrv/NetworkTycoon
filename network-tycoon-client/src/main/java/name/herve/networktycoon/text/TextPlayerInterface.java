package name.herve.networktycoon.text;

import java.util.List;

import name.herve.networktycoon.Goal;
import name.herve.networktycoon.Player;
import name.herve.networktycoon.engine.PlayerInterface;

public class TextPlayerInterface extends PlayerInterface {

	public TextPlayerInterface(Player player) {
		super(player);
	}

	@Override
	public List<Goal> actionChooseGoalsToKeep(List<Goal> goals, int min) {
		out("choose at least " + min + " goals :");
		for (Goal g : goals) {
			out(" - " + g);
		}
		return goals;
	}

	private void out(String msg) {
		TextInterface.out("Player - " + getPlayer().getName() + " : " + msg);
	}



}

package name.herve.networktycoon.text;

import name.herve.networktycoon.Player;
import name.herve.networktycoon.ResourceListFixedSize;
import name.herve.networktycoon.engine.BoardInterface;
import name.herve.networktycoon.engine.PlayerInterface;

public class TextBoardInterface extends BoardInterface {

	public TextBoardInterface() {
		super();
	}

	@Override
	public PlayerInterface getPlayerInterface(Player player) {
		return new TextPlayerInterface(player);
	}

	private void out(String msg) {
		TextInterface.out("Board - " + msg);
	}

	@Override
	public void updateShownResources(ResourceListFixedSize shownResources) {
		out("ShownResources : " + shownResources);
	}

	@Override
	public void warningTooManyJockersInShownResources() {
		out("ShownResources : too many jockers !");
	}

}

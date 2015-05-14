package name.herve.networktycoon.jbox2d;

import name.herve.bastod.tools.GameException;
import name.herve.networktycoon.Board;
import name.herve.networktycoon.BoardFactory;

import org.jbox2d.dynamics.World;

public class TestFactory {
	int step = 0;

	World world;

	public void init(World world) {
		this.world = world;

		try {
			BoardFactory bf = new BoardFactory();
			bf.setSeed(6000l);
			Board b = bf.getRandomBoard();
//			Board b = bf.getSampleBoard();
			bf.jbox2dInitWorld(world, b);

		} catch (GameException e) {
			e.printStackTrace();
		}
	}
}

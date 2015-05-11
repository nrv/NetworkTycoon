package name.herve.networktycoon;

import java.io.File;

import name.herve.bastod.tools.math.Dimension;
import name.herve.networktycoon.gui.BoardGuiTool;

public class TestBoardFactory {
	

	public static void main(String[] args) {
		// Dimension[] screens = new Dimension[] { new Dimension(640, 480), new
		// Dimension(1024, 768), new Dimension(1280, 1024), new Dimension(1920,
		// 1080) };
		Dimension[] screens = new Dimension[] { new Dimension(1024, 768) };
		int nb = 10;
		File tempDir = new File("/tmp/nt");
		tempDir.mkdirs();

		try {
			for (int i = 0; i < nb; i++) {
				BoardFactory bf = new BoardFactory();
				long seed = 1000 * i;
				bf.setSeed(seed);
				Board b = bf.getRandomBoard();
//				Board b = bf.getSampleBoard();

				for (Dimension s : screens) {
					BoardGuiTool tool = new BoardGuiTool(b.getDimension(), s);

					tool.debug(b, new File(tempDir, "tbf_final_" + i + "_" + seed + "_" + s.getW() + "x" + s.getH() + ".jpg"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

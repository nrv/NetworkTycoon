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

import java.io.File;

import name.herve.bastod.tools.math.Dimension;
import name.herve.networktycoon.gui.BoardGuiTool;

/**
 * @author Nicolas HERVE
 */
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

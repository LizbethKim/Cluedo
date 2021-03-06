package cluedo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cluedo.model.Board;
import cluedo.ui.CluedoUI;

public class Main {

	public static void main(String[] args){
		try {
			Board board = createBoardFromFile("/board.txt");
			new CluedoUI(board);
		} catch (IOException e) {e.printStackTrace();}
	}


	/**
	 * Creates the board from a board.txt file.
	 * @param filename File name of the board file
	 * @return A Board object
	 * @throws IOException For if invalid file
	 */
	public static Board createBoardFromFile(String filename) throws IOException {
		InputStream styleFile = Main.class.getResourceAsStream(
	            filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(styleFile));
		ArrayList<String> lines = new ArrayList<String>();
		int width = -1;
		String line;
		while((line = br.readLine()) != null) {
			lines.add(line);
			// now sanity check

			if(width == -1) {
				width = line.length();
			} else if(width != line.length()) {
				br.close();
				throw new IllegalArgumentException("Input file \"" + filename + "\" is malformed; line " + lines.size() + " incorrect width.");
			}
		}
		br.close();
		Board board = new Board(width,lines.size());
		for(int y=0;y!=lines.size();++y) {
			line = lines.get(y);
			for(int x=0;x!=width;++x) {
				char c = line.charAt(x);
				switch (c) {
					case 'W' :
						board.addWall(x, y);
						break;
					case 'H':
						board.addHallway(x, y);
						break;
					case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
						board.addRoom(x, y, Character.getNumericValue(c));
						break;
					case 'M':
						board.addMiddleRoom(x, y);
						break;
				}
			}
		}

		return board;
	}
}

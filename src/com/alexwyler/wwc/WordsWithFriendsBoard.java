package com.alexwyler.wwc;

import java.util.HashMap;
import java.util.Map;

public class WordsWithFriendsBoard extends BoardDescription {

	static Space[][] spaces = new Space[15][15];

	static {
		spaces[3][0] = Space.TRIPLE_WORD;
		spaces[6][0] = Space.TRIPLE_LETTER;
		spaces[2][1] = Space.DOUBLE_LETTER;
		spaces[5][1] = Space.DOUBLE_WORD;
		spaces[1][2] = Space.DOUBLE_LETTER;
		spaces[4][2] = Space.DOUBLE_LETTER;
		spaces[0][3] = Space.TRIPLE_WORD;
		spaces[3][3] = Space.TRIPLE_LETTER;
		spaces[6][0] = Space.TRIPLE_LETTER;
		spaces[2][4] = Space.DOUBLE_LETTER;
		spaces[6][4] = Space.DOUBLE_LETTER;
		spaces[1][5] = Space.DOUBLE_WORD;
		spaces[5][5] = Space.TRIPLE_LETTER;
		spaces[0][6] = Space.TRIPLE_LETTER;
		spaces[4][6] = Space.DOUBLE_LETTER;
		BoardDescription.fillArrayFromUpperRight(spaces);
		spaces[7][3] = Space.DOUBLE_WORD;
		spaces[3][7] = Space.DOUBLE_WORD;
		spaces[11][7] = Space.DOUBLE_WORD;
		spaces[7][11] = Space.DOUBLE_WORD;
	}

	private static Map<Character, Integer> points = new HashMap<Character, Integer>();
	static {
		points.put('A', 1);
		points.put('B', 4);
		points.put('C', 4);
		points.put('D', 2);
		points.put('E', 1);
		points.put('F', 4);
		points.put('G', 3);
		points.put('H', 3);
		points.put('I', 1);
		points.put('J', 10);
		points.put('K', 5);
		points.put('L', 2);
		points.put('M', 4);
		points.put('N', 2);
		points.put('O', 1);
		points.put('P', 4);
		points.put('Q', 10);
		points.put('R', 1);
		points.put('S', 1);
		points.put('T', 1);
		points.put('U', 2);
		points.put('V', 5);
		points.put('W', 4);
		points.put('X', 8);
		points.put('Y', 3);
		points.put('Z', 10);
	}

	@Override
	public int getHeight() {
		return spaces.length;
	}

	@Override
	public int getWidth() {
		return spaces.length;
	}

	@Override
	public Space getSpace(int x, int y) {
		return spaces[x][y];
	}

	@Override
	public int getLetterValue(char c) {
		return points.get(Character.toUpperCase(c));
	}

	@Override
	public int numTilesForScrabble() {
		return 7;
	}

	@Override
	public int scrabbleBonus() {
		return 35;
	}

}

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

	private static Map<Tile, Integer> points = new HashMap<Tile, Integer>();
	static {
		points.put(new Tile('A'), 1);
		points.put(new Tile('B'), 4);
		points.put(new Tile('C'), 4);
		points.put(new Tile('D'), 2);
		points.put(new Tile('E'), 1);
		points.put(new Tile('F'), 4);
		points.put(new Tile('G'), 3);
		points.put(new Tile('H'), 3);
		points.put(new Tile('I'), 1);
		points.put(new Tile('J'), 10);
		points.put(new Tile('K'), 5);
		points.put(new Tile('L'), 2);
		points.put(new Tile('M'), 4);
		points.put(new Tile('N'), 2);
		points.put(new Tile('O'), 1);
		points.put(new Tile('P'), 4);
		points.put(new Tile('Q'), 10);
		points.put(new Tile('R'), 1);
		points.put(new Tile('S'), 1);
		points.put(new Tile('T'), 1);
		points.put(new Tile('U'), 2);
		points.put(new Tile('V'), 5);
		points.put(new Tile('W'), 4);
		points.put(new Tile('X'), 8);
		points.put(new Tile('Y'), 3);
		points.put(new Tile('Z'), 10);
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
	public int getLetterValue(Tile t) {
		if (t.wildcard || t.isBlank()) {
			return 0;
		}
		try {
			return Character.toUpperCase(t.c);
		} catch (RuntimeException e) {
			System.out.print("Unable to score tile " + t);
			throw e;
		}
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

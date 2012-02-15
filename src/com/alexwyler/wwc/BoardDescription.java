package com.alexwyler.wwc;

public abstract class BoardDescription {
	
	public abstract int getHeight();
	
	public abstract int getWidth();
	
	public abstract Space getSpace(int x, int y);
	
	public abstract int getLetterValue(char c);
	
	public abstract int numTilesForScrabble();
	
	public abstract int scrabbleBonus();
	
	static void fillArrayFromUpperRight(Space[][] spaces) {
		for (int x = 0; x < spaces.length / 2; x++) {
			int ylength = spaces[x].length;
			for (int y = 0; y < spaces[x].length / 2; y++) {
				spaces[spaces.length - x - 1][y] = spaces[x][y];
				spaces[spaces.length - x -1][ylength - y - 1] = spaces[x][y];
				spaces[x][ylength - y - 1] = spaces[x][y];
			}
		}
	}
	
}

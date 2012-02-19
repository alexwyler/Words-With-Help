package com.alexwyler.wwc;

public class SomeGames {

	public static Tile[][] stacey = new Tile[][] {
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, c('T'), null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Tile[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, };

	static {
		stacey = flipXY(stacey);
	}
	
	public static Tile[][] flipXY(Tile[][] board) {
		Tile[][] stacey2 = new Tile[15][15];
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				stacey2[x][y] = board[y][x];
			}
		}
		return stacey2;
	}

	public static Tile c(char c) {
		return new Tile(c);
	}
	
}

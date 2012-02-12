package com.alexwyler.wwc;

public class SomeGames {

	public static Character[][] mom = new Character[][] {
		new Character[] { null, null, null, 'M',  null, null, null, null, null, null, null, null, null, null, null },
		new Character[] { null, 'O',  'V',  'A',  'R',  'Y',  null, null, null, null, null, null, null, null, null },
		new Character[] { null, 'O',  null, 'R',  null, null, null, null, null, null, null, null, null, null, null },
		new Character[] { null, 'Z',  null, 'C',  'H',  'U',  'R',  'R',  null, null, null, null, null, null, null },
		new Character[] { null, 'E',  null, 'H',  null, null, null, 'I',  null, null, null, null, null, null, null },
		new Character[] { null, 'D',  null, null, null, 'L' , 'I' , 'T',  'R' , 'E' , null, null, null, null, null },
		new Character[] { null, null, null, null, null, 'I' , null, 'E',  null, null, 'F' , null, null, null, null },
		new Character[] { null, null, null, null, null, 'M' , null, 'S',  'H' , 'A' , 'L' , 'E' , null, null, null },
		new Character[] { null, null, null, null, 'Q' , 'I' , null, null, null, null, 'A' , null, 'S' , null, null },
		new Character[] { null, null, null, null, 'I' , 'T' , null, null, null, 'S' , 'T' , 'E' , 'E' , 'P' , null },
		new Character[] { null, null, null, null, null, null, null, null, null, null, 'S' , null, 'X' , null, null },
		new Character[] { null, null, null, null, null, null, null, null, null, 'J' , null, 'W' , 'Y' , 'N' , 'S'  },
		new Character[] { null, null, null, null, null, null, null, null, null, 'A' , null, 'A' , null, null, null },
		new Character[] { null, null, null, null, null, null, null, null, null, 'W' , 'O' , 'N' , 'K' , null, null },
		new Character[] { null, null, null, null, null, null, null, null, null, null, null, 'T' , null, null, null } };
	
	static {
		mom = flipXY(mom);
	}

	public static Character[][] stacey = new Character[][] {
			new Character[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null },
			new Character[] { null, null, null, null, null, null, null, null, null, 'G' , null, null, null, null, null },
			new Character[] { null, null, null, null, null, null, null, null, null, 'O' , null, null, null, null, 'V'  },
			new Character[] { null, null, null, null, null, null, null, 'L' , 'A' , 'R' , 'D' , null, null, 'L',  'A'  },
			new Character[] { null, null, null, null, null, null, 'P' , 'I' , 'T' , null, null, null, 'B' , 'I',  'T'  },
			new Character[] { null, null, null, null, null, 'Q' , 'I' , 'S' , null, null, null, 'T',  'A' , 'T',  null },
			new Character[] { null, null, null, null, null, null, 'N' , null, 'G' , null, 'J',  'O',  'T' , 'A' , null },
			new Character[] { null, null, null, null, 'R' , 'E' , 'S' , 'H',  'A',  'V',  'E',  'N',  null, 'S' , null },
			new Character[] { 'T' , null, null, 'D' , null, null, null, 'U',  'N' , null, 'U',  'N',  null, null, 'H'  },
			new Character[] { 'I' , 'F',  null, 'U' , null, 'D',  'A',  'R',  'E',  'R',  null, 'E',  null, 'W' , 'O'  },
			new Character[] { 'C' , 'O',  'P' , 'E' , null, 'O',  null, 'L',  null, 'E' , null, null, null, 'H' , 'E'  },
			new Character[] { null, 'R',  'E',  'L',  'A',  'X',  'E' , 'S',  null, 'I' , 'M' , 'A' , 'G' , 'E' , 'D'  },
			new Character[] { null, 'K',  'A' , null, 'H' , 'I',  'N' , null, null, 'F' , 'U' , 'B' , null, 'R' , null },
			new Character[] { null, 'Y',  null, null, null, 'E',  'S' , null, 'O' , 'Y' , null, null, 'Z' , 'E' , 'D'  },
			new Character[] { null, null, 'I' , 'C' , 'E' , 'S' , null, null, 'W' , null, null, 'M' , 'A' , null, null } };

	static {
		stacey = flipXY(stacey);
	}

	public static Character[][] flipXY(Character[][] board) {
		Character[][] stacey2 = new Character[15][15];
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				stacey2[x][y] = board[y][x];
			}
		}
		return stacey2;
	}

}

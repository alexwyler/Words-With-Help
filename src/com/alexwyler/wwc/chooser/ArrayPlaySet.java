package com.alexwyler.wwc.chooser;

import com.alexwyler.wwc.Point;

public class ArrayPlaySet extends PlaySet {

	private Character[][] letters = new Character[15][15];

	@Override
	protected void _place(Point p, Character c) {
		letters[p.x][p.y] = c;
	}

	@Override
	protected void _remove(Point p) {
		letters[p.x][p.y] = null;
	}

	@Override
	public Character getLetter(Point p) {
		return letters[p.x][p.y];
	}

}

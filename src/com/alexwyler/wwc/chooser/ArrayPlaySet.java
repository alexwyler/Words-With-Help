package com.alexwyler.wwc.chooser;

import com.alexwyler.wwc.Point;
import com.alexwyler.wwc.Tile;

public class ArrayPlaySet extends PlaySet {

	private Tile[][] letters = new Tile[15][15];

	@Override
	protected void _place(Point p, Tile c) {
		letters[p.x][p.y] = c;
	}

	@Override
	protected void _remove(Point p) {
		letters[p.x][p.y] = null;
	}

	@Override
	public Tile getLetter(Point p) {
		return letters[p.x][p.y];
	}

}

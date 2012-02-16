package com.alexwyler.wwc.chooser;

import java.util.HashMap;
import java.util.Map;

import com.alexwyler.wwc.Point;
import com.alexwyler.wwc.Tile;

public class MapPlaySet extends PlaySet {

	private Map<Point, Tile> characters = new HashMap<Point, Tile>();
	
	public MapPlaySet() {
		super();
	}
	
	@SuppressWarnings("deprecation")
	public MapPlaySet(Map<Point, Tile> cakes) {
		super(cakes);
	}

	@Override
	public Tile getLetter(Point p) {
		return characters.get(p);
	}

	@Override
	protected void _remove(Point p) {
		characters.remove(p);
	}

	@Override
	protected void _place(Point p, Tile c) {
		characters.put(p, c);
	}

}

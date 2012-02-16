package com.alexwyler.wwc.chooser;

import java.util.HashMap;
import java.util.Map;

import com.alexwyler.wwc.Point;

public class MapPlaySet extends PlaySet {

	private Map<Point, Character> characters = new HashMap<Point, Character>();
	
	public MapPlaySet() {
		super();
	}
	
	@SuppressWarnings("deprecation")
	public MapPlaySet(Map<Point, Character> cakes) {
		super(cakes);
	}

	@Override
	public Character getLetter(Point p) {
		return characters.get(p);
	}

	@Override
	protected void _place(Point p, Character c) {
		characters.put(p, c);
	}

	@Override
	protected void _remove(Point p) {
		characters.remove(p);
	}

}

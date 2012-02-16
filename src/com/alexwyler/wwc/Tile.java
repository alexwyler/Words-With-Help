package com.alexwyler.wwc;

public class Tile {

	public char c;

	public boolean wildcard = false;

	public boolean isBlank() {
		return c == 0;
	}

	public Tile(char c, boolean wildcard) {
		this.c = c;
		this.wildcard = wildcard;
	}

	public Tile(char c) {
		this.c = c;
	}

	@Override
	public String toString() {
		String tile = "" + c;
		if (wildcard) {
			tile += " (wildcard)";
		}
		return tile;
	}

}

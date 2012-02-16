package com.alexwyler.wwc;

public class Tile {

	public char c;

	public boolean wildcard = false;

	public boolean isBlank() {
		return c == 0 || c == '*';
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

	@Override
	public int hashCode() {
		return (int) c + (wildcard ? Integer.MAX_VALUE >> 1 : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (c != other.c)
			return false;
		return true;
	}

}

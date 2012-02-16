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
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		result = prime * result + (wildcard ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (c != other.c)
			return false;
		if (wildcard != other.wildcard)
			return false;
		return true;
	}

}

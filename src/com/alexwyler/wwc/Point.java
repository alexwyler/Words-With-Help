package com.alexwyler.wwc;

public class Point extends java.awt.Point implements Comparable<Point> {

	private static final long serialVersionUID = -6438635516715318772L;

	public Point(int i, int y) {
		super(i, y);
	}

	@Override
	public int compareTo(Point o) {
		if (this.y > o.y) {
			return -1;
		} else if (this.y < o.y) {
			return 1;
		} else if (this.x < o.x) {
			return -1;
		} else {
			return 1;
		}
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}

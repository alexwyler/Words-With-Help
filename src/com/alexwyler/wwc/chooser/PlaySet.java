package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alexwyler.wwc.PlayingBoard;
import com.alexwyler.wwc.Point;
import com.alexwyler.wwc.Tile;

public abstract class PlaySet {

	private List<Point> points = new ArrayList<Point>(15);

	public PlaySet() {

	}

	@Deprecated
	public PlaySet(Map<Point, Tile> moves) {
		for (Entry<Point, Tile> move : moves.entrySet()) {
			place(move.getKey(), move.getValue());
		}
	}

	public abstract Tile getLetter(Point p);

	public void place(Point p, Tile c) {
		points.add(p);
		_place(p, c);
	}

	protected abstract void _place(Point p, Tile c);

	public void filterExistingLetters(PlayingBoard game) {
		List<Point> filtered = new ArrayList<Point>();
		for (Point p : points) {
			if (game.letterAt(p) == null) {
				filtered.add(p);
			} else {
				_remove(p);
			}
		}
		points = filtered;
	}

	protected abstract void _remove(Point p);

	public PlaySet merge(PlaySet other) {
		for (Point p : other.getPoints()) {
			place(p, other.getLetter(p));
		}
		return this;
	}

	public String toWord() {
		StringBuffer buffer = new StringBuffer();
		for (Point p : points) {
			buffer.append(getLetter(p).c);
		}
		return buffer.toString();
	}

	public String toString() {
		String str = "[";
		for (Point p : points) {
			str += "(" + p.x + "," + p.y + ")=>'" + getLetter(p) + "', ";
		}
		str += "]";
		return str;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		for (Point point : points) {
			result += getLetter(point).hashCode();
		}
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
		PlaySet other = (PlaySet) obj;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		return true;
	}

}

package com.alexwyler.wwc.chooser;

import java.awt.Point;
import java.util.Map;

public class PlayOption implements Comparable<PlayOption> {
	public Map<Point, Character> getMove() {
		return move;
	}

	public void setMove(Map<Point, Character> move) {
		this.move = move;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	Map<Point, Character> move;

	int score;

	public PlayOption(Map<Point, Character> move, int score) {
		this.move = move;
		this.score = score;
	}

	@Override
	public int compareTo(PlayOption o) {
		return o.score - this.score;
	}

}

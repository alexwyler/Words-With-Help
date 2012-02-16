package com.alexwyler.wwc.chooser;


public class PlayOption implements Comparable<PlayOption> {
	public PlaySet getMove() {
		return move;
	}

	public void setMove(PlaySet move) {
		this.move = move;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	PlaySet move;

	int score;

	public PlayOption(PlaySet move, int score) {
		this.move = move;
		this.score = score;
	}

	@Override
	public int compareTo(PlayOption o) {
		return o.score - this.score;
	}

}

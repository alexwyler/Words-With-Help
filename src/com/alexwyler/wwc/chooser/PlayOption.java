package com.alexwyler.wwc.chooser;

public class PlayOption implements Comparable<PlayOption> {

	PlaySet move;

	int score;

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

	public PlayOption(PlaySet move, int score) {
		this.move = move;
		this.score = score;
	}

	@Override
	public int compareTo(PlayOption o) {
		return o.score - this.score;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((move == null) ? 0 : move.hashCode());
		result = prime * result + score;
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
		PlayOption other = (PlayOption) obj;
		if (move == null) {
			if (other.move != null)
				return false;
		} else if (!move.equals(other.move))
			return false;
		if (score != other.score)
			return false;
		return true;
	}
	
	

}

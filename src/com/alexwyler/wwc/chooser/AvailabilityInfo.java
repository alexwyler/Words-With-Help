package com.alexwyler.wwc.chooser;

import java.awt.Point;

import com.alexwyler.wwc.PlayingBoard;

public class AvailabilityInfo {

	int left;
	int right;
	int up;
	int down;

	public AvailabilityInfo(int left, int right, int up, int down) {
		this.left = left;
		this.right = right;
		this.up = up;
		this.down = down;
	}

	public static AvailabilityInfo getAvailabilityInfo(PlayingBoard game,
			Point p) {

		int left = 0;
		int right = 0;
		int up = 0;
		int down = 0;
		Point cur = new Point(p.x - 1, p.y);
		while (game.inBounds(cur)
				&& game.getPlayedLetters()[cur.x][cur.y] == null) {
			left++;
			cur = new Point(cur.x - 1, cur.y);
		}

		cur = new Point(p.x + 1, p.y);
		while (game.inBounds(cur)
				&& game.getPlayedLetters()[cur.x][cur.y] == null) {
			right++;
			cur = new Point(cur.x + 1, cur.y);
		}

		cur = new Point(p.x, p.y - 1);
		while (game.inBounds(cur)
				&& game.getPlayedLetters()[cur.x][cur.y] == null) {
			up++;
			cur = new Point(cur.x, cur.y - 1);
		}

		cur = new Point(p.x, p.y + 1);
		while (game.inBounds(cur)
				&& game.getPlayedLetters()[cur.x][cur.y] == null) {
			down++;
			cur = new Point(cur.x, cur.y + 1);
		}

		return new AvailabilityInfo(left, right, up, down);
	}
}

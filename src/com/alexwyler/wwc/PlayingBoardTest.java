package com.alexwyler.wwc;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class PlayingBoardTest {

	PlayingBoard game = null;

	@Before
	public void setup() throws FileNotFoundException {
		Dictionary dict = Dictionary.getInstance(new File("words.txt"));
		BoardDescription board = new WordsWithFriendsBoard();
		game = new PlayingBoard(board, dict);
	}

	@Test
	public void testOK() throws InvalidPlayException, GameStateException {
		Map<Point, Tile> cakes = new HashMap<Point, Tile>();
		cakes.put(new Point(7, 7), new Tile('C'));
		cakes.put(new Point(7, 8), new Tile('A'));
		cakes.put(new Point(7, 9), new Tile('K'));
		cakes.put(new Point(7, 10), new Tile('E'));
		cakes.put(new Point(7, 11), new Tile('S'));

		game.placeLetters(cakes);
		int score = game.commitPending();
		Assert.assertEquals(score, 24);
	}
}

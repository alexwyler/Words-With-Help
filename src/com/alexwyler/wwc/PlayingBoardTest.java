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
		Map<Point, Character> cakes = new HashMap<Point, Character>();
		cakes.put(new Point(7, 7), 'C');
		cakes.put(new Point(7, 8), 'A');
		cakes.put(new Point(7, 9), 'K');
		cakes.put(new Point(7, 10), 'E');
		cakes.put(new Point(7, 11), 'S');

		game.placeLetters(cakes);
		int score = game.commitPending();
		Assert.assertEquals(score, 24);
	}
	
	@Test
	public void testOK2() throws InvalidPlayException, GameStateException {
		Map<Point, Character> cakes = new HashMap<Point, Character>();
		cakes.put(new Point(7, 7), 'C');
		cakes.put(new Point(7, 8), 'A');
		cakes.put(new Point(7, 9), 'K');
		cakes.put(new Point(7, 10), 'E');
		cakes.put(new Point(7, 11), 'S');

		game.placeLetters(cakes);
		int score = game.commitPending();
		Assert.assertEquals(score, 24);
		
		Map<Point, Character> trees = new HashMap<Point, Character>();
		trees.put(new Point(5, 10), 'T');
		trees.put(new Point(6, 10), 'R');
		trees.put(new Point(8, 10), 'E');
		trees.put(new Point(9, 10), 'S');
		
		game.placeLetters(trees);
		score = game.commitPending();
		Assert.assertEquals(score, 7);
	}

	@Test
	public void testNotOk() throws Exception {
		Map<Point, Character> cakes = new HashMap<Point, Character>();
		cakes.put(new Point(7, 7), 'C');
		cakes.put(new Point(7, 8), 'A');
		cakes.put(new Point(7, 9), 'K');
		cakes.put(new Point(7, 10), 'E');
		cakes.put(new Point(7, 11), 'S');
		cakes.put(new Point(9, 9), 'C');

		game.placeLetters(cakes);
		try {
			game.commitPending();
		} catch (InvalidPlayException e) {
			System.out.println(e);
			return;
		}

		throw new Exception("Illegal Move should throw");
	}
}

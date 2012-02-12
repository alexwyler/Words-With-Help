package com.alexwyler.wwc;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PlayingBoard {

	Character[][] playedLetters = null;
	BoardDescription board;
	Dictionary dict;
	int numPlaysCommitted = 0;

	List<Point> pendingPoints = new ArrayList<Point>();

	public PlayingBoard(BoardDescription board, Dictionary dict) {
		this.board = board;
		playedLetters = new Character[board.getWidth()][board.getHeight()];
		this.dict = dict;
	}

	public PlayingBoard(BoardDescription board, Dictionary dict,
			Character[][] current, int numTurns) {
		this.board = board;
		playedLetters = new Character[board.getWidth()][board.getHeight()];
		this.dict = dict;
		playedLetters = current;
		numPlaysCommitted = numTurns;
	}

	public void placeLetter(Point p, char c) throws InvalidPlayException {
		if (!inBounds(p)) {
			throw new InvalidPlayException("Letter '" + c
					+ "' placed out of bounds (" + p.x + "," + p.y + ")");
		} else if (playedLetters[p.x][p.y] != null) {
			throw new InvalidPlayException("Letter '" + c
					+ "' placed on existing letter (" + p.x + "," + p.y + ")");
		}
		pendingPoints.add(p);
		playedLetters[p.x][p.y] = c;
	}

	public void placeLetters(Map<Point, Character> letters)
			throws InvalidPlayException {
		for (Point point : letters.keySet()) {
			placeLetter(point, letters.get(point));
		}
	}

	public void assertPendingIsValid() throws InvalidPlayException,
			GameStateException {
		List<List<Point>> createdWords;
		createdWords = getCreatedWords();

		boolean wordIncludesExistingLetter = false;
		if (numPlaysCommitted > 0) {
			wordIncludesExistingLetter = true;
		}
		for (List<Point> word : createdWords) {
			orderLetters(word);
			String wordStr = wordToString(word);
			if (!dict.isInDictionary(wordStr)) {
				throw new InvalidPlayException("'" + wordStr
						+ "' is not a valid word");
			}

			for (Point point : word) {
				if (!pendingPoints.contains(point)) {
					wordIncludesExistingLetter = true;
					break;
				}
			}
		}

		if (!wordIncludesExistingLetter) {
			throw new InvalidPlayException(
					"Word is formed without using any pre-existing letters");
		}

		int x = pendingPoints.get(0).x;
		int y = pendingPoints.get(0).y;
		boolean horizontalRow = true;
		boolean verticalRow = true;
		for (Point point : pendingPoints) {
			if (point.x != x) {
				horizontalRow = false;
			}
			if (point.y != y) {
				verticalRow = false;
			}
		}

		if (!(verticalRow || horizontalRow)) {
			throw new InvalidPlayException(
					"Must play letters in a straight line, horizontally or vertically");
		}
	}

	private String wordToString(List<Point> word) {
		orderLetters(word);
		String ret = "";
		for (Point point : word) {
			ret += playedLetters[point.x][point.y];
		}
		return ret;
	}

	private void orderLetters(List<Point> word) {
		Collections.sort(word, new Comparator<Point>() {

			@Override
			public int compare(Point a, Point b) {
				if (a.x < b.x || a.y < b.y) {
					return -1;
				} else {
					return 1;
				}
			}

		});
	}

	private List<List<Point>> getCreatedWords() throws GameStateException {
		if (pendingPoints.isEmpty()) {
			throw new GameStateException("Pending letters queue is empty");
		}

		List<List<Point>> createdWords = new ArrayList<List<Point>>();
		for (Point point : pendingPoints) {
			List<List<Point>> containingWords = getWordsContaining(point);
			for (List<Point> word : containingWords) {
				if (!wordContainedInList(word, createdWords)) {
					createdWords.add(word);
				}
			}
		}

		return createdWords;
	}

	private boolean wordContainedInList(List<Point> needle,
			List<List<Point>> haystack) {
		for (List<Point> word : haystack) {
			if (word.containsAll(needle)) {
				return true;
			}
		}
		return false;
	}

	List<List<Point>> getWordsContaining(Point point) {
		List<List<Point>> ret = new ArrayList<List<Point>>();

		List<Point> horiz = new ArrayList<Point>();
		Point curpoint = point;
		while (inBounds(curpoint)
				&& playedLetters[curpoint.x][curpoint.y] != null) {
			horiz.add(curpoint);
			curpoint = new Point(curpoint.x - 1, curpoint.y);
		}

		curpoint = new Point(point.x + 1, point.y);
		while (inBounds(curpoint)
				&& playedLetters[curpoint.x][curpoint.y] != null) {
			horiz.add(curpoint);
			curpoint = new Point(curpoint.x + 1, curpoint.y);
		}

		if (horiz.size() > 1) {
			ret.add(horiz);
		}

		List<Point> vert = new ArrayList<Point>();
		curpoint = point;
		while (inBounds(curpoint)
				&& playedLetters[curpoint.x][curpoint.y] != null) {
			vert.add(curpoint);
			curpoint = new Point(curpoint.x, curpoint.y - 1);
		}

		curpoint = new Point(point.x, point.y + 1);
		while (inBounds(curpoint)
				&& playedLetters[curpoint.x][curpoint.y] != null) {
			vert.add(curpoint);
			curpoint = new Point(curpoint.x, curpoint.y + 1);
		}

		if (vert.size() > 1) {
			ret.add(vert);
		}
		return ret;
	}

	public int commitPending() throws InvalidPlayException, GameStateException {
		assertPendingIsValid();
		int score = scorePending();
		pendingPoints = new ArrayList<Point>();
		numPlaysCommitted++;
		return score;
	}

	public void discardPending() {
		for (Point p : pendingPoints) {
			playedLetters[p.x][p.y] = null;
		}
		pendingPoints = new ArrayList<Point>();
	}

	public boolean inBounds(Point point) {
		int x = point.x;
		int y = point.y;
		return !(x < 0 || x >= board.getWidth() || y < 0 || y >= board
				.getHeight());
	}

	public int scorePending() throws GameStateException {
		List<List<Point>> createdWords = getCreatedWords();
		int score = 0;
		for (List<Point> word : createdWords) {
			score += scoreWord(word);
		}
		return score;
	}

	private int scoreWord(List<Point> word) {
		Space wordMod = null;
		int score = 0;
		for (Point p : word) {
			char letter = playedLetters[p.x][p.y];
			Space letterMod = board.getSpace(p.x, p.y);
			int letterScore = board.getLetterValue(letter);
			if (pendingPoints.contains(p)) {
				if (letterMod == Space.DOUBLE_LETTER) {
					letterScore *= 2;
				} else if (letterMod == Space.TRIPLE_LETTER) {
					letterScore *= 3;
				} else if (letterMod == Space.DOUBLE_WORD
						|| letterMod == Space.TRIPLE_WORD) {
					wordMod = letterMod;
				}
			}
			score += letterScore;
		}

		if (wordMod == Space.DOUBLE_WORD) {
			score *= 2;
		} else if (wordMod == Space.TRIPLE_WORD) {
			score *= 3;
		}
		return score;
	}

	public Character[][] getPlayedLetters() {
		return playedLetters;
	}

	public void setPlayedLetters(Character[][] playedLetters) {
		this.playedLetters = playedLetters;
	}

	public BoardDescription getBoard() {
		return board;
	}

	public void setBoard(BoardDescription board) {
		this.board = board;
	}

	public Dictionary getDict() {
		return dict;
	}

	public void setDict(Dictionary dict) {
		this.dict = dict;
	}

	public int getNumPlaysCommitted() {
		return numPlaysCommitted;
	}

	public void setNumPlaysCommitted(int numPlaysCommitted) {
		this.numPlaysCommitted = numPlaysCommitted;
	}

	public List<Point> getPendingPoints() {
		return pendingPoints;
	}

	public void setPendingPoints(List<Point> pendingPoints) {
		this.pendingPoints = pendingPoints;
	}

	public List<Point> getAllPoints() {
		List<Point> points = new ArrayList<Point>();
		for (int y = 0; y < board.getWidth(); y++) {
			for (int x = 0; x < board.getHeight(); x++) {
				if (playedLetters[x][y] != null)
					points.add(new Point(x, y));
			}
		}
		return points;
	}

	public void printBoard(boolean showSpaces) {
		for (int y = 0; y < board.getWidth(); y++) {
			for (int x = 0; x < board.getHeight(); x++) {
				Character c = playedLetters[x][y];
				if (c == null) {
					System.out.print("__");
				} else {
					if (pendingPoints.contains(new Point(x, y))) {
						System.out.print(Character.toLowerCase(c));
						System.out.print("*");
					} else {
						System.out.print(Character.toUpperCase(c));
						System.out.print("_");
					}
				}
				if (showSpaces) {
					Space s = board.getSpace(x, y);
					if (s == Space.TRIPLE_LETTER) {
						System.out.print("tl");
					} else if (s == Space.TRIPLE_WORD) {
						System.out.print("tw");
					} else if (s == Space.DOUBLE_LETTER) {
						System.out.print("dl");
					} else if (s == Space.DOUBLE_WORD) {
						System.out.print("dw");
					} else {
						System.out.print("__");
					}
				}
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}

}

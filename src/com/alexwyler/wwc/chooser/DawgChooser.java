package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alexwyler.wwc.GameStateException;
import com.alexwyler.wwc.InvalidPlayException;
import com.alexwyler.wwc.PlayingBoard;
import com.alexwyler.wwc.Point;
import com.alexwyler.wwc.Tile;
import com.alexwyler.wwc.dawg.DawgNode;

public class DawgChooser extends PlayChooser {

	boolean isComplete = false;
	List<PlayOption> options = new ArrayList<PlayOption>();
	List<PlaySet> legalMoves = new ArrayList<PlaySet>();
	List<PlayOption> curOptions = new ArrayList<PlayOption>();
	Map<Point, Set<Character>> crossSets = new HashMap<Point, Set<Character>>();
	DawgNode dawg;
	PlayingBoard game;
	List<Tile> tiles;

	public DawgChooser(PlayingBoard game, List<Tile> tiles) {
		this.game = game;
		this.tiles = tiles;
		this.dawg = DawgNode.getInstance();
	}

	@Override
	public List<PlayOption> getCurrentOptions() {
		return options;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public List<PlayOption> getOptions() throws GameStateException {
		getAcrossOptions();
		System.out.println("Flipping board to retry... ");
		game.flip();
		System.out.println("DONE.");
		getAcrossOptions();
		game.flip();

		System.out.println("legal moves: " + legalMoves);
		for (PlaySet move : legalMoves) {
			try {
				game.placeLetters(move);
				String vio = game.getPendingViolation();
				if (vio == null) {
					int score = game.scorePending();
					options.add(new PlayOption(move, score));
				} else {
				}
			} catch (InvalidPlayException e) {
				e.printStackTrace();
			}
			game.discardPending();
		}

		isComplete = true;
		return options;
	}

	private void getAcrossOptions() {
		System.out.print("Calculating cross-checks... ");
		calculateCrossChecks();
		System.out.println("DONE.");
		for (int y = 0; y < 15; y++) {

			List<Point> anchors = new ArrayList<Point>();
			System.out.print("Calculating anchors for y=" + y + "... ");
			for (int x = 0; x < 15; x++) {
				Point p = new Point(x, y);
				Point right = new Point(x + 1, y);
				Point down = new Point(x, y + 1);
				if (game.inBounds(right) && game.letterAt(right) != null
						|| game.inBounds(down) && game.letterAt(down) != null) {
					anchors.add(p);
				}
			}
			System.out.println("DONE, anchors=" + anchors);

			for (Point p : anchors) {
				int limit = 0;
				Point left = new Point(p.x - 1, p.y);
				while (game.inBounds(left) && game.letterAt(left) == null) {
					limit++;
					left = new Point(left.x - 1, left.y);
				}
				System.out.print("Calculating moves for anchor " + p
						+ " with left space " + limit + "... ");
				leftPart(new LinkedList<Tile>(), dawg, limit, p);
				System.out.println("DONE.");
			}
		}
	}

	private int indexOfCharInRack(char c) {
		for (int i = 0; i < tiles.size(); i++) {
			if (c == tiles.get(i).c) {
				return i;
			}
		}
		return -1;
	}

	private void leftPart(LinkedList<Tile> partial, DawgNode node, int limit,
			Point anchor) {
		extendRight(partial, node, anchor);
		if (limit > 0) {
			// TODO: iterate over rack instead of edge here, because blanks
			// don't trigger word score
			for (Character c : node.edges.keySet()) {
				int rackIndex = indexOfCharInRack(c);
				if (rackIndex > 0) {
					Tile removed = tiles.remove(rackIndex);
					partial.addLast(removed);
					DawgNode next = node.edges.get(c);
					leftPart(partial, next, limit - 1, anchor);
					tiles.add(removed);
					partial.removeLast();
				}
			}
		}
	}

	private void extendRight(LinkedList<Tile> partial, DawgNode node,
			Point square) {
		if (game.inBounds(square) && game.letterAt(square) == null) {
			if (node.terminal) {
				recordMove(partial, square);
			}
			for (Character c : node.edges.keySet()) {
				int rackIndex = indexOfCharInRack(c);
				Set<Character> crossSet = crossSets.get(square);
				if (rackIndex > 0 && crossSet != null && crossSet.contains(c)) {
					Tile removed = tiles.remove(rackIndex);
					partial.addLast(removed);
					DawgNode next = node.edges.get(c);
					Point right = new Point(square.x + 1, square.y);
					extendRight(partial, next, right);
					partial.removeLast();
					tiles.add(removed);
				}
			}
		} else {
			Tile letter = game.letterAt(square);
			if (node.edges.containsKey(letter.c)) {
				partial.addLast(letter);
				DawgNode next = node.edges.get(letter.c);
				Point right = new Point(square.x + 1, square.y);
				extendRight(partial, next, right);
				partial.removeLast();
			}
		}
	}

	private void calculateCrossChecks() {
		for (int y = 0; y < 15; y++) {
			for (int x = 0; x < 15; x++) {
				Point p = new Point(x, y);
				if (game.letterAt(p) == null) {
					crossSets.put(p, getCrossCheck(p));
				}
			}
		}
	}

	private Set<Character> getCrossCheck(Point p) {
		Set<Character> ret = null;
		if (game.inBounds(new Point(p.x, p.y + 1))) {
			ret = new HashSet<Character>();
			for (char c = 'a'; c <= 'z'; c++) {
				DawgNode cur = dawg;
				boolean valid = true;
				Point next = new Point(p.x, p.y + 1);
				while (cur != null) {
					if (!game.inBounds(next) || game.letterAt(next) == null) {
						valid = cur.terminal;
						break;
					}
					Character nextChar = game.letterAt(next).c;
					if (!cur.edges.containsKey(nextChar)) {
						valid = false;
						break;
					} else {
						next = new Point(next.x, next.y + 1);
						cur = cur.edges.get(nextChar);
					}
				}

				if (valid) {
					ret.add(c);
				}
			}
		}
		return ret;
	}

	private void recordMove(List<Tile> word, Point terminator) {
		PlaySet move = new MapPlaySet();
		Point cur = new Point(terminator.x - 1, terminator.y);
		for (int i = word.size() - 1; i >= 0; i--) {
			if (game.letterAt(cur) == null) {
				move.place(cur, word.get(i));
			}
		}
		recordMove(move);
	}

	private void recordMove(PlaySet option) {
		if (game.isFlipped()) {
			PlaySet normalized = new MapPlaySet();
			for (Point p : option.getPoints()) {
				normalized._place(new Point(p.y, p.x), option.getLetter(p));
			}
			option = normalized;
		}

		legalMoves.add(option);
	}
}

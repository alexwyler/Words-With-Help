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
	Set<PlaySet> seenMoves = new HashSet<PlaySet>();
	Map<Point, Set<Character>> crossSets = new HashMap<Point, Set<Character>>();
	DawgNode dawg;
	PlayingBoard game;
	List<Tile> tiles;

	public DawgChooser(PlayingBoard game, List<Tile> tiles, DawgNode dawg) {
		this.game = game;
		List<Tile> nonBlanks = new LinkedList<Tile>();
		for (Tile tile : tiles) {
			if (!tile.wildcard) {
				nonBlanks.add(tile);
			}
		}
		this.tiles = nonBlanks;
		this.dawg = dawg;
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
		game.flip();
		getAcrossOptions();
		game.flip();

		for (PlaySet move : legalMoves) {
			if (seenMoves.contains(move)) {
				continue;
			} else {
				seenMoves.add(move);
			}
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
		calculateCrossChecks();
		for (int y = 0; y < 15; y++) {

			List<Point> anchors = new ArrayList<Point>();
			for (int x = 0; x < 15; x++) {
				Point p = new Point(x, y);
				Point right = new Point(x + 1, y);
				Point left = new Point(x - 1, y);
				Point down = new Point(x, y + 1);
				if (game.inBounds(right) && game.letterAt(right) != null
						|| game.inBounds(down) && game.letterAt(down) != null
						|| game.inBounds(left) && game.letterAt(left) != null) {
					anchors.add(p);
				}
			}

			for (Point p : anchors) {
				int limit = 0;
				Point left = new Point(p.x - 1, p.y);
				while (game.inBounds(left) && game.letterAt(left) == null) {
					limit++;
					left = new Point(left.x - 1, left.y);
				}
				if (limit == 0) {
					Point cur = new Point(p.x - 1, p.y);
					while (game.inBounds(cur) && game.letterAt(cur) != null) {
						cur = new Point(cur.x - 1, cur.y);
					}
					LinkedList<Tile> partial = new LinkedList<Tile>();
					DawgNode node = dawg;
					cur = new Point(cur.x + 1, cur.y);
					while (game.inBounds(cur) && game.letterAt(cur) != null) {
						partial.add(game.letterAt(cur));
						node = node.edges.get(Character.toLowerCase(game
								.letterAt(cur).c));
						cur = new Point(cur.x + 1, cur.y);
					}
					extendRight(partial, node, p);
				}
				leftPart(new LinkedList<Tile>(), dawg, limit, p);
			}
		}
	}

	private int indexOfCharInRack(char c) {
		for (int i = 0; i < tiles.size(); i++) {
			if (Character.toLowerCase(c) == tiles.get(i).c) {
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
				if (rackIndex >= 0) {
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
		if (game.inBounds(square)) {
			if (game.letterAt(square) == null) {
				if (node.terminal) {
					recordMove(partial, square);
				}
				Set<Character> crossSet = crossSets.get(square);
				for (Character c : node.edges.keySet()) {
					int rackIndex = indexOfCharInRack(c);
					if (rackIndex >= 0
							&& (crossSet == null || crossSet.contains(c))) {
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
				if (node.edges.containsKey(Character.toLowerCase(letter.c))) {
					partial.addLast(letter);
					DawgNode next = node.edges.get(Character
							.toLowerCase(letter.c));
					Point right = new Point(square.x + 1, square.y);
					extendRight(partial, next, right);
					partial.removeLast();
				}
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
		Point down = new Point(p.x, p.y + 1);
		if (game.inBounds(down) && game.letterAt(down) != null) {
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
		int i = word.size() - 1;
		while (i >= 0 || (game.inBounds(cur) && game.letterAt(cur) != null)) {
			if (game.inBounds(cur) && game.letterAt(cur) == null) {
				move.place(cur, word.get(i));
			}
			i--;
			cur = new Point(cur.x - 1, cur.y);
		}
		recordMove(move);
	}

	private void recordMove(PlaySet option) {
		if (game.isFlipped()) {
			PlaySet normalized = new MapPlaySet();
			for (Point p : option.getPoints()) {
				normalized.place(new Point(p.y, p.x), option.getLetter(p));
			}
			option = normalized;
		}
		legalMoves.add(option);
	}
}

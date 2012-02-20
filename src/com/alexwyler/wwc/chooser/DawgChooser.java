package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.Collections;
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
	List<PlayOption> options = Collections
			.synchronizedList(new ArrayList<PlayOption>());
	List<PlayOption> curOptions = new ArrayList<PlayOption>();
	Set<PlaySet> seenMoves = new HashSet<PlaySet>();
	Map<Point, Set<Character>> crossSets = new HashMap<Point, Set<Character>>();
	DawgNode dawg;
	PlayingBoard game;
	List<Tile> tiles;

	public DawgChooser(PlayingBoard game, List<Tile> tiles, DawgNode dawg) {
		this.game = game;
		this.tiles = tiles;
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
		isComplete = true;
		return options;
	}

	private void getAcrossOptions() throws GameStateException {
		for (int y = 0; y < 15; y++) {

			List<Point> anchors = new ArrayList<Point>();

			if (!game.isBoardEmpty()) {
				for (int x = 0; x < 15; x++) {
					Point p = new Point(x, y);
					Point right = new Point(x + 1, y);
					Point left = new Point(x - 1, y);
					Point down = new Point(x, y + 1);
					Point up = new Point(x, y - 1);
					if (game.inBounds(right) && game.letterAt(right) != null
							|| game.inBounds(down)
							&& game.letterAt(down) != null
							|| game.inBounds(left)
							&& game.letterAt(left) != null || game.inBounds(up)
							&& game.letterAt(up) != null) {
						anchors.add(p);
					}
				}
			} else {
				for (int x = 1; x <= 7; x++) {
					anchors.add(new Point(x, y));
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
						node = node.getChild(game.letterAt(cur));
						cur = new Point(cur.x + 1, cur.y);
					}
					extendRight(partial, node, p);
				}
				leftPart(new LinkedList<Tile>(), dawg, limit, p);
			}
		}
	}

	private void leftPart(LinkedList<Tile> partial, DawgNode node, int limit,
			Point anchor) throws GameStateException {
		extendRight(partial, node, anchor);
		if (limit > 0) {
			for (int i = 0; i < tiles.size(); i++) {
				Tile removed = tiles.remove(i);
				List<Tile> toChecks = new ArrayList<Tile>();
				if (removed.wildcard) {
					for (char c = 'a'; c <= 'z'; c++) {
						toChecks.add(new Tile(c, true));
					}
				} else {
					toChecks.add(removed);
				}
				for (Tile toCheck : toChecks) {
					DawgNode next = node.getChild(toCheck);
					if (next != null) {
						partial.addLast(toCheck);
						leftPart(partial, next, limit - 1, anchor);
						partial.removeLast();
					}
				}
				tiles.add(removed);
			}
		}
	}

	private void extendRight(LinkedList<Tile> partial, DawgNode node,
			Point square) throws GameStateException {
		if (node.terminal) {
			recordMove(partial, square);
		}
		if (game.inBounds(square) && game.letterAt(square) == null) {
			Set<Character> crossSet = crossSets.get(square);

			for (int i = 0; i < tiles.size(); i++) {
				Tile removed = tiles.remove(i);
				List<Tile> toChecks = new ArrayList<Tile>();
				if (removed.wildcard) {
					for (char c = 'a'; c <= 'z'; c++) {
						toChecks.add(new Tile(c, true));
					}
				} else {
					toChecks.add(removed);
				}
				for (Tile toCheck : toChecks) {
					if (crossSet != null
							&& !crossSet.contains(Character
									.toLowerCase(toCheck.c))) {
						continue;
					}
					DawgNode next = node.getChild(toCheck);
					if (next != null) {
						partial.addLast(toCheck);
						Point right = new Point(square.x + 1, square.y);
						extendRight(partial, next, right);
						partial.removeLast();
					}
				}
				tiles.add(i, removed);
			}
		} else {
			Tile letter = game.letterAt(square);
			DawgNode next = node.getChild(letter);
			if (next != null) {
				partial.addLast(letter);
				Point right = new Point(square.x + 1, square.y);
				extendRight(partial, next, right);
				partial.removeLast();
			}
		}
	}

	private void recordMove(List<Tile> word, Point terminator)
			throws GameStateException {
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

	private void recordMove(PlaySet option) throws GameStateException {
		int score = -1;
		try {
			game.placeLetters(option);
			String vio = game.getPendingViolation();
			if (vio == null) {
				score = game.scorePending();
			}
		} catch (InvalidPlayException e) {
			e.printStackTrace();
		}
		game.discardPending();

		if (score < 0 || seenMoves.contains(option)) {
			return;
		} else {
			seenMoves.add(option);
		}

		if (game.isFlipped()) {
			PlaySet normalized = new MapPlaySet();
			for (Point p : option.getPoints()) {
				normalized.place(new Point(p.y, p.x), option.getLetter(p));
			}
			option = normalized;
		}
		options.add(new PlayOption(option, score));

	}
}

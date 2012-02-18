package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alexwyler.wwc.GameStateException;
import com.alexwyler.wwc.InvalidPlayException;
import com.alexwyler.wwc.PlayingBoard;
import com.alexwyler.wwc.Point;
import com.alexwyler.wwc.Tile;

public class NaiveChooser extends PlayChooser {

	PlayingBoard game;
	List<Tile> tiles;
	Set<List<Tile>> curAnagrams;
	Set<List<Tile>> allAnagrams = new HashSet<List<Tile>>();
	List<PlayOption> options = Collections
			.synchronizedList(new ArrayList<PlayOption>());
	boolean isComplete = false;

	public NaiveChooser(PlayingBoard game, List<Tile> tiles) {
		this.game = game;
		this.tiles = tiles;
	}

	private List<List<Tile>> explodeRacks(List<Tile> tiles) {
		LinkedList<List<Tile>> toTest = new LinkedList<List<Tile>>();
		List<List<Tile>> results = new LinkedList<List<Tile>>();
		toTest.add(tiles);
		while (!toTest.isEmpty()) {
			List<Tile> rackToTest = toTest.removeFirst();
			int wildCardIdx;
			for (wildCardIdx = 0; wildCardIdx < rackToTest.size(); wildCardIdx++) {
				Tile tile = rackToTest.get(wildCardIdx);
				if (tile.wildcard && tile.isBlank()) {
					break;
				}
			}
			if (wildCardIdx < rackToTest.size()) {
				for (char c = 'a'; c <= 'z'; c++) {
					List<Tile> exploded = new ArrayList<Tile>(rackToTest);
					exploded.set(wildCardIdx, new Tile(c, true));
					toTest.add(exploded);
				}
			} else {
				results.add(rackToTest);
			}
		}
		return results;
	}

	@Override
	public List<PlayOption> getOptions() throws GameStateException {
		List<Point> preExisting = game.getAllPoints();
		if (preExisting.isEmpty()) {
			preExisting.add(new Point(game.getBoard().getWidth() / 2, game
					.getBoard().getHeight() / 2));
		}
		List<List<Tile>> racksToTest = explodeRacks(tiles);
		for (List<Tile> curRack : racksToTest) {
			Set<Point> testedPoints = new HashSet<Point>();
			Collection<PlaySet> moves = new HashSet<PlaySet>();
			curAnagrams = Anagramer.powerList(curRack);
			LinkedList<Point> pointsToCheck = new LinkedList<Point>();
			for (Point point : preExisting) {
				pointsToCheck.add(point);
				pointsToCheck.add(new Point(point.x - 1, point.y));
				pointsToCheck.add(new Point(point.x + 1, point.y));
				pointsToCheck.add(new Point(point.x, point.y - 1));
				pointsToCheck.add(new Point(point.x, point.y + 1));
				while (!pointsToCheck.isEmpty()) {
					Point pointToCheck = pointsToCheck.removeFirst();
					if (testedPoints.contains(pointToCheck)) {
						continue;
					} else {
						testedPoints.add(pointToCheck);
						List<PlaySet> availableMoves = getAllAvailableMoves(pointToCheck);
						for (PlaySet move : availableMoves) {
							if (moves.contains(move)) {
								continue;
							} else {
								moves.add(move);
							}
							try {
								game.placeLetters(move);
								
								String vio = game.getPendingViolation();
								if (vio == null) {
									int score = game.scorePending();
									options.add(new PlayOption(move, score));
								} else {
									if (vio.toLowerCase().contains("ab")) {
										game.printBoard(false);
									}
								}
							} catch (InvalidPlayException e) {
								e.printStackTrace();
							}
							game.discardPending();
						}
					}
				}
			}
		}
		Collections.sort(options);
		isComplete = true;
		return options;
	}

	public List<PlaySet> getAllAvailableMoves(Point point) {
		List<PlaySet> moves = new ArrayList<PlaySet>();
		int j;
		for (List<Tile> anagram : curAnagrams) {
			for (int i = 0; i <= anagram.size(); i++) {
				PlaySet downCachedVal = new MapPlaySet();
				PlaySet leftCachedVal = new MapPlaySet();
				PlaySet rightCachedVal = new MapPlaySet();
				PlaySet upCachedVal = new MapPlaySet();

				// place tiles above
				Point placePoint = new Point(point.x, point.y);
				List<Tile> toPlace = anagram.subList(0, i);
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Tile letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						upCachedVal.place(placePoint, toPlace.get(j++));
					} else {
						upCachedVal.place(placePoint, letter);
					}
					placePoint = new Point(placePoint.x, placePoint.y - 1);
				}

				// place tiles below
				placePoint = new Point(point.x, point.y + 1);
				toPlace = anagram.subList(i, anagram.size());
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Tile letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						downCachedVal.place(placePoint, toPlace.get(j++));
					} else {
						downCachedVal.place(placePoint, letter);
					}
					placePoint = new Point(placePoint.x, placePoint.y + 1);
				}

				// place tiles to the left
				placePoint = new Point(point.x, point.y);
				toPlace = anagram.subList(0, i);
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Tile letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						leftCachedVal.place(placePoint, toPlace.get(j++));
					} else {
						leftCachedVal.place(placePoint, letter);
					}
					placePoint = new Point(placePoint.x - 1, placePoint.y);
				}

				// place tiles right
				placePoint = new Point(point.x + 1, point.y);
				toPlace = anagram.subList(i, anagram.size());
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Tile letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						rightCachedVal.place(placePoint, toPlace.get(j++));
					} else {
						rightCachedVal.place(placePoint, letter);
					}
					placePoint = new Point(placePoint.x + 1, placePoint.y);
				}

				PlaySet horiz = rightCachedVal.merge(leftCachedVal);
				PlayingBoard.orderLetters(horiz.getPoints());

				PlaySet vert = downCachedVal.merge(upCachedVal);
				PlayingBoard.orderLetters(horiz.getPoints());

				if (game.getDict().isInDictionary(horiz.toWord())) {
					horiz.filterExistingLetters(game);
					moves.add(horiz);
				}

				if (game.getDict().isInDictionary(vert.toWord())) {
					vert.filterExistingLetters(game);
					moves.add(vert);
				}
			}
		}
		return moves;
	}

	@Override
	public List<PlayOption> getCurrentOptions() {
		return options;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}
}

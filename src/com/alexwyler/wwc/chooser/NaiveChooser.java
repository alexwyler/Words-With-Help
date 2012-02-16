package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alexwyler.wwc.GameStateException;
import com.alexwyler.wwc.InvalidPlayException;
import com.alexwyler.wwc.PlayingBoard;
import com.alexwyler.wwc.Point;

public class NaiveChooser extends PlayChooser {

	PlayingBoard game;
	List<Character> tiles;
	Set<List<Character>> allAnagrams;
	HashSet<Point> testedPoints = new HashSet<Point>();
	HashSet<PlaySet> moves = new HashSet<PlaySet>();

	public NaiveChooser(PlayingBoard game, List<Character> tiles) {
		this.game = game;
		this.tiles = tiles;
	}

	@Override
	public List<PlayOption> getOptions() throws GameStateException {
		allAnagrams = Anagramer.powerList(tiles);
		List<PlayOption> options = new LinkedList<PlayOption>();
		List<Point> preExisting = game.getAllPoints();
		if (preExisting.isEmpty()) {
			preExisting.add(new Point(game.getBoard().getWidth() / 2, game
					.getBoard().getHeight() / 2));
		}
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
							if (game.getPendingViolation() == null) {
								int score = game.scorePending();
								options.add(new PlayOption(move, score));
							}
						} catch (InvalidPlayException e) {
							e.printStackTrace();
						}
						game.discardPending();
					}
				}
			}
		}
		int maxNum = Math.min(10, options.size());
		Collections.sort(options);
		options = options.subList(0, maxNum);
		return options;
	}

	public List<PlaySet> getAllAvailableMoves(Point point) {
		List<PlaySet> moves = new ArrayList<PlaySet>();
		int j;
		long cacheTime = 0;
		long nonCacheTime = 0;
		for (List<Character> anagram : allAnagrams) {
			for (int i = 0; i <= anagram.size(); i++) {
				PlaySet downCachedVal = new MapPlaySet();
				PlaySet leftCachedVal = new MapPlaySet();
				PlaySet rightCachedVal = new MapPlaySet();
				PlaySet upCachedVal = new MapPlaySet();
				long time1 = System.currentTimeMillis();

				// place tiles above
				Point placePoint = new Point(point.x, point.y);
				List<Character> toPlace = anagram.subList(0, i);
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Character letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						upCachedVal.place(placePoint, toPlace.get(j++));
					} else {
						upCachedVal.place(placePoint, letter);
					}
					placePoint = new Point(placePoint.x, placePoint.y - 1);
				}

				long time2 = System.currentTimeMillis();

				// place tiles below
				placePoint = new Point(point.x, point.y + 1);
				toPlace = anagram.subList(i, anagram.size());
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Character letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						downCachedVal.place(placePoint, toPlace.get(j++));
					} else {
						downCachedVal.place(placePoint, letter);
					}
					placePoint = new Point(placePoint.x, placePoint.y + 1);
				}

				long time3 = System.currentTimeMillis();
				cacheTime += time2 - time1;
				nonCacheTime += time3 - time2;

				// place tiles to the left
				placePoint = new Point(point.x, point.y);
				toPlace = anagram.subList(0, i);
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Character letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
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
					Character letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						rightCachedVal.place(placePoint, toPlace.get(j++));
					} else {
						rightCachedVal.place(placePoint, letter);
					}
					placePoint = new Point(placePoint.x + 1, placePoint.y);
				}

				PlaySet horiz = rightCachedVal.merge(leftCachedVal);
				PlayingBoard.orderLetters(horiz.getPoints());

				if (game.getDict().isInDictionary(horiz.toWord())) {
					horiz.filterExistingLetters(game);
					moves.add(horiz);
				}

				PlaySet vert = downCachedVal.merge(upCachedVal);
				PlayingBoard.orderLetters(horiz.getPoints());
				if (game.getDict().isInDictionary(vert.toWord())) {
					vert.filterExistingLetters(game);
					moves.add(vert);
				}
			}
		}
		return moves;
	}
}

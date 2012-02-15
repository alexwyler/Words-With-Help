package com.alexwyler.wwc.chooser;

import java.awt.Point;
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

public class NaiveChooser extends PlayChooser {

	PlayingBoard game;
	List<Character> tiles;
	Set<List<Character>> allAnagrams;
	HashSet<Point> testedPoints = new HashSet<Point>();
	HashSet<Map<Point, Character>> moves = new HashSet<Map<Point, Character>>();

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
					AvailabilityInfo availInfo = AvailabilityInfo
							.getAvailabilityInfo(game, pointToCheck);
					long time1 = System.currentTimeMillis();
					List<Map<Point, Character>> availableMoves = getAllAvailableMoves(
							availInfo, pointToCheck);
					long time2 = System.currentTimeMillis();
					for (Map<Point, Character> move : availableMoves) {
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
							// Something went wrong
							e.printStackTrace();
						}
						game.discardPending();
					}
					System.out.println("time finding moves: " + (time2 - time1));
				}
			}
		}
		int maxNum = Math.min(10, options.size());
		Collections.sort(options);
		options = options.subList(0, maxNum);
		return options;
	}

	public List<Map<Point, Character>> getAllAvailableMoves(
			AvailabilityInfo availInfo, Point point) {
		List<Map<Point, Character>> moves = new ArrayList<Map<Point, Character>>();
		for (List<Character> anagram : allAnagrams) {
			for (int i = 0; i <= anagram.size(); i++) {
				Map<Point, Character> vert = new HashMap<Point, Character>();
				// place tiles above
				Point placePoint = new Point(point.x, point.y);
				List<Character> toPlace = anagram.subList(0, i);
				int j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Character letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						vert.put(placePoint, toPlace.get(j++));
					} else {
						vert.put(placePoint, letter);
					}
					placePoint = new Point(placePoint.x, placePoint.y - 1);
				}

				// place tiles below
				placePoint = new Point(point.x, point.y + 1);
				toPlace = anagram.subList(i, anagram.size());
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Character letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						vert.put(placePoint, toPlace.get(j++));
					} else {
						vert.put(placePoint, letter);
					}
					placePoint = new Point(placePoint.x, placePoint.y + 1);
				}

				Map<Point, Character> horiz = new HashMap<Point, Character>();
				// place tiles to the left
				placePoint = new Point(point.x, point.y);
				toPlace = anagram.subList(0, i);
				j = 0;
				while (j < toPlace.size() && game.inBounds(placePoint)) {
					Character letter = game.getPlayedLetters()[placePoint.x][placePoint.y];
					if (letter == null) {
						horiz.put(placePoint, toPlace.get(j));
					} else {
						horiz.put(placePoint, letter);
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
						horiz.put(placePoint, toPlace.get(j++));
					} else {
						horiz.put(placePoint, letter);
					}
					placePoint = new Point(placePoint.x + 1, placePoint.y);
				}

				List<Point> orderedHorizPoints = new LinkedList<Point>(
						horiz.keySet());
				PlayingBoard.orderLetters(orderedHorizPoints);
				StringBuffer horizWord = new StringBuffer();
				for (Point p : orderedHorizPoints) {
					horizWord.append(horiz.get(p));
				}

				if (game.getDict().isInDictionary(horizWord.toString())) {
					for (int k = 0; k < orderedHorizPoints.size(); k++) {
						Point p = orderedHorizPoints.get(k);
						if (game.letterAt(p) != null) {
							horiz.remove(p);
						}
					}
					moves.add(horiz);
				}
				
				List<Point> orderedVertPoints = new LinkedList<Point>(
						vert.keySet());
				PlayingBoard.orderLetters(orderedVertPoints);
				StringBuffer vertWord = new StringBuffer();
				for (Point p : orderedVertPoints) {
					vertWord.append(vert.get(p));
				}

				if (game.getDict().isInDictionary(vertWord.toString())) {
					for (int k = 0; k < orderedVertPoints.size(); k++) {
						Point p = orderedVertPoints.get(k);
						if (game.letterAt(p) != null) {
							vert.remove(p);
						}
					}
					moves.add(vert);
				}
			}
		}
		return moves;
	}
}

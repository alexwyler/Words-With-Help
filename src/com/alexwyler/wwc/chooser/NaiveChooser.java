package com.alexwyler.wwc.chooser;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alexwyler.wwc.GameStateException;
import com.alexwyler.wwc.InvalidPlayException;
import com.alexwyler.wwc.PlayingBoard;

public class NaiveChooser extends PlayChooser {

	PlayingBoard game;
	List<Character> tiles;

	public NaiveChooser(PlayingBoard game, List<Character> tiles) {
		this.game = game;
		this.tiles = tiles;
	}

	@Override
	public List<PlayOption> getOptions() throws GameStateException {
		List<PlayOption> options = new ArrayList<PlayOption>();
		List<Point> preExisting = game.getAllPoints();
		for (Point point : preExisting) {
			AvailabilityInfo availInfo = AvailabilityInfo.getAvailabilityInfo(
					game, point);
			List<Map<Point, Character>> availableMoves = getAllAvailableMoves(
					availInfo, point);
			for (Map<Point, Character> move : availableMoves) {
				try {
					game.placeLetters(move);
					game.assertPendingIsValid();
					int score = game.scorePending();
					options.add(new PlayOption(move, score));
				} catch (InvalidPlayException e) {
					// Invalid play, discard
				}
				game.discardPending();
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
		
		Character c = game.getPlayedLetters()[point.x][point.y];
		Set<List<Character>> allAnagrams = Anagramer.powerList(tiles);
		for (List<Character> anagram : allAnagrams) {
			for (int i = 0; i <= anagram.size(); i++) {
				List<Character> inserted = new ArrayList<Character>(anagram);
				inserted.add(i, c);
				
				boolean canFitHoriz = false;
				if (availInfo.left >= i && availInfo.right >= anagram.size() - i - 1) {
					canFitHoriz = true;
				}
				
				boolean canFitVert = false;
				if (availInfo.up >= i && availInfo.down >= anagram.size() - i - 1) {
					canFitVert = true;
				}
				
				if (canFitHoriz || canFitVert) {
					if (game.getDict().isInDictionary(inserted)) {
						Map<Point, Character> vert = new HashMap<Point, Character>();
						Map<Point, Character> horiz = new HashMap<Point, Character>();
						for (int j = 0; j < inserted.size(); j++) {
							if (i != j) {
								vert.put(new Point(point.x, point.y - i + j),
										inserted.get(j));
								horiz.put(new Point(point.x - i + j, point.y),
										inserted.get(j));
							}
						}
						if (canFitVert) {
							moves.add(vert);
						}
						if (canFitHoriz) {
							moves.add(horiz);
						}
					}
				}
			}
		}
		return moves;
	}
}

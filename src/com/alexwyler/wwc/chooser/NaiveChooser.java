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
import com.alexwyler.wwc.Tile;

public class NaiveChooser extends PlayChooser {

	PlayingBoard game;
	List<Tile> tiles;
	Set<List<Tile>> allAnagrams;
	List<PlayOption> options = Collections
			.synchronizedList(new ArrayList<PlayOption>());
	boolean isComplete = false;

	public NaiveChooser(PlayingBoard game, List<Tile> tiles) {
		this.game = game;
		this.tiles = tiles;
	}

	@Override
	public List<PlayOption> getOptions() throws GameStateException {
		allAnagrams = Anagramer.powerList(tiles);
		List<Point> preExisting = game.getAllPoints();
		for (Point point : preExisting) {
			AvailabilityInfo availInfo = AvailabilityInfo.getAvailabilityInfo(
					game, point);
			List<Map<Point, Tile>> availableMoves = getAllAvailableMoves(
					availInfo, point);
			for (Map<Point, Tile> move : availableMoves) {
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
		Collections.sort(options);
		isComplete = true;
		return options;
	}

	public List<Map<Point, Tile>> getAllAvailableMoves(
			AvailabilityInfo availInfo, Point point) {
		List<Map<Point, Tile>> moves = new ArrayList<Map<Point, Tile>>();

		Tile c = game.getPlayedLetters()[point.x][point.y];
		for (List<Tile> anagram : allAnagrams) {
			for (int i = 0; i <= anagram.size(); i++) {
				List<Tile> inserted = new ArrayList<Tile>(anagram);
				inserted.add(i, c);

				boolean canFitHoriz = false;
				if (availInfo.left >= i
						&& availInfo.right >= anagram.size() - i - 1) {
					canFitHoriz = true;
				}

				boolean canFitVert = false;
				if (availInfo.up >= i
						&& availInfo.down >= anagram.size() - i - 1) {
					canFitVert = true;
				}

				if (canFitHoriz || canFitVert) {
					if (game.getDict().isInDictionary(inserted)) {
						Map<Point, Tile> vert = new HashMap<Point, Tile>();
						Map<Point, Tile> horiz = new HashMap<Point, Tile>();
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

	@Override
	public List<PlayOption> getCurrentOptions() {
		return options;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}
}

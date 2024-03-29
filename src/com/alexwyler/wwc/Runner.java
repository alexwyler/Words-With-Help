package com.alexwyler.wwc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.alexwyler.wwc.chooser.DawgChooser;
import com.alexwyler.wwc.chooser.PlayChooser;
import com.alexwyler.wwc.chooser.PlayOption;
import com.alexwyler.wwc.dawg.DawgNode;

public class Runner {

	public static void main(String[] args) throws FileNotFoundException,
			InvalidPlayException, GameStateException {
		Dictionary dict = Dictionary.getInstance(new File(
				"WebContent/words.txt"));
		BoardDescription board = new WordsWithFriendsBoard();
		Tile[][] current = SomeGames.stacey;
		PlayingBoard game = new PlayingBoard(board, dict, current, 1);
		List<Tile> chars = new ArrayList<Tile>();
		chars.add(new Tile('T'));
		chars.add(new Tile('A'));
		chars.add(new Tile('P'));

		DawgNode dawg = DawgNode.getInstance(new File("WebContent/words.txt"));
		PlayChooser chooser = new DawgChooser(game, chars, dawg);
		List<PlayOption> options = chooser.getOptions();

		if (!options.isEmpty()) {
			for (PlayOption option : options) {
				game.placeLetters(option.getMove());
				game.printBoard(true);
				game.discardPending();
			}
		} else {
			System.out.println("no results");
		}

	}
}

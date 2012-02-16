package com.alexwyler.wwc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.alexwyler.wwc.chooser.NaiveChooser;
import com.alexwyler.wwc.chooser.PlayChooser;
import com.alexwyler.wwc.chooser.PlayOption;

public class Runner {

	public static void main(String[] args) throws FileNotFoundException,
			InvalidPlayException, GameStateException {
		Dictionary dict = Dictionary.getInstance(new File(
				"WebContent/words.txt"));
		BoardDescription board = new WordsWithFriendsBoard();
		Tile[][] current = SomeGames.stacey;
		PlayingBoard game = new PlayingBoard(board, dict, current, 1);
		
		List<Tile> chars = new ArrayList<Tile>();
		chars.add(new Tile('t'));
		chars.add(new Tile('o'));
		
		PlayChooser chooser = new NaiveChooser(game, chars);
		List<PlayOption> options = chooser.getOptions();

		if (!options.isEmpty()) {
			for (PlayOption option : options) {
				game.placeLetters(option.getMove());
				game.printBoard(true);
				System.out.println("score: " + game.scorePending());
				game.discardPending();
			}
		} else {
			System.out.println("no results");
		}

	}
}

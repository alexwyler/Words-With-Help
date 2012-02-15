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
		Character[][] current = SomeGames.test3;
		PlayingBoard game = new PlayingBoard(board, dict, current);
		
		game.printBoard(false);
			
		List<Character> chars = new ArrayList<Character>();
		chars.add('c');
		chars.add('h');
		chars.add('e');
		chars.add('a');
		chars.add('t');
		chars.add('a');
		chars.add('h');
		chars.add('s');

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

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
		Dictionary dict = Dictionary.getInstance(new File("words.txt"));
		BoardDescription board = new WordsWithFriendsBoard();
		Character[][] current = SomeGames.stacey;
		PlayingBoard game = new PlayingBoard(board, dict, current, 1);
		
		List<Character> chars = new ArrayList<Character>();
		chars.add('t');
		chars.add('o');
		
//		chars.add('t');
//		chars.add('e');
//		chars.add('e');
//		chars.add('i');
//		chars.add('i');
//		chars.add('u');
//		chars.add('l');

		PlayChooser chooser = new NaiveChooser(game, chars);
		List<PlayOption> options = chooser.getOptions();
		
		for (PlayOption option : options) {
			game.placeLetters(option.getMove());
			game.printBoard(true);
			System.out.println("score: " + game.scorePending());
			game.discardPending();
		}

	}
}

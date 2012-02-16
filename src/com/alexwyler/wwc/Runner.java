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
		chars.add(new Tile('a'));
		chars.add(new Tile('d'));
		chars.add(new Tile('e'));
		chars.add(new Tile('f'));
		chars.add(new Tile('c'));
		chars.add(new Tile('d'));
		chars.add(new Tile('q'));
		chars.add(new Tile('x'));
		
		PlayChooser chooser = new NaiveChooser(game, chars);
		List<PlayOption> options = chooser.getOptions();

		if (!options.isEmpty()) {
			for (PlayOption option : options) {
				game.placeLetters(option.getMove());
				//game.printBoard(true);
				game.discardPending();
			}
		} else {
			System.out.println("no results");
		}

	}
}

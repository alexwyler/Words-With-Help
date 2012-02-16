package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alexwyler.wwc.Tile;

public class Anagramer {

	public static Set<List<Tile>> powerList(List<Tile> originalSet) {
		Set<List<Tile>> sets = new HashSet<List<Tile>>();
		if (originalSet.size() == 1) {
			Tile tile = originalSet.get(0);
			if (tile.wildcard && tile.isBlank()) {
				for (char c = 'a'; c <= 'z'; c++) {
					List<Tile> wildcard = new ArrayList<Tile>();
					wildcard.add(new Tile(c, true));
					sets.add(wildcard);
				}
			} else {
				sets.add(new ArrayList<Tile>(originalSet));
			}
			return sets;
		}
		for (int i = 0; i < originalSet.size(); i++) {
			Tile head = originalSet.remove(i);
			List<Tile> rest = new ArrayList<Tile>(originalSet);
			originalSet.add(i, head);
			for (List<Tile> set : powerList(rest)) {
				List<Tile> newSet = new ArrayList<Tile>();
				newSet.add(head);
				newSet.addAll(set);
				sets.add(newSet);
				sets.add(set);
			}
		}
		return sets;
	}
}
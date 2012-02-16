package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alexwyler.wwc.Tile;

public class Anagramer {

	private static Set<List<Tile>> _powerList(List<Tile> originalSet) {
		Set<List<Tile>> sets = new HashSet<List<Tile>>();
		if (originalSet.size() == 1) {
			sets.add(new ArrayList<Tile>(originalSet));
			return sets;
		}
		for (int i = 0; i < originalSet.size(); i++) {
			Tile head = originalSet.remove(i);
			List<Tile> rest = new ArrayList<Tile>(originalSet);
			originalSet.add(i, head);
			for (List<Tile> set : _powerList(rest)) {
				List<Tile> newSet = new ArrayList<Tile>();
				newSet.add(head);
				newSet.addAll(set);
				sets.add(newSet);
				sets.add(set);
			}
		}
		return sets;
	}

	public static Set<List<Tile>> powerList(List<Tile> originalSet) {
		return explodeWildCards(_powerList(originalSet));
	}

	private static Set<List<Tile>> explodeWildCards(Set<List<Tile>> unexploded) {
		Set<List<Tile>> results = new HashSet<List<Tile>>();
		for (List<Tile> anagram : unexploded) {
			for (int i = 0; i < anagram.size(); i++) {
				if (anagram.get(i).wildcard) {
					for (char c = 'a'; c <= 'z'; c++) {
						List<Tile> newList = new ArrayList<Tile>(anagram);
						newList.set(i, new Tile(c, true));
						results.add(newList);
					}
				} else {
					results.add(anagram);
				}
			}
		}
		return results;
	}
}
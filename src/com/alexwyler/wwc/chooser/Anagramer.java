package com.alexwyler.wwc.chooser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Anagramer {

	public static Set<List<Character>> powerList(List<Character> originalSet) {
		Set<List<Character>> sets = new HashSet<List<Character>>();
		if (originalSet.size() == 1) {
			sets.add(new ArrayList<Character>(originalSet));
			return sets;
		}
		for (int i = 0; i < originalSet.size(); i++) {
			Character head = originalSet.remove(i);
			List<Character> rest = new ArrayList<Character>(originalSet);
			originalSet.add(i, head);
			for (List<Character> set : powerList(rest)) {
				List<Character> newSet = new ArrayList<Character>();
				newSet.add(head);
				newSet.addAll(set);
				sets.add(newSet);
				sets.add(set);
			}
		}
		return sets;
	}
}
package com.alexwyler.wwc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class Dictionary {

	HashSet<String> words = new HashSet<String>();
	static HashMap<String, Dictionary> instances = new HashMap<String, Dictionary>();

	public static Dictionary getInstance(File file)
			throws FileNotFoundException {
		Dictionary instance = instances.get(file.getName());
		if (instance == null) {
			instance = new Dictionary(file);
			instances.put(file.getName(), instance);
		}
		return instance;
	}

	private Dictionary(File file) throws FileNotFoundException {
		Scanner in = new Scanner(file);
		while (in.hasNextLine()) {
			words.add(in.nextLine());
		}
	}

	public boolean isInDictionary(String string) {
		return words.contains(string.toLowerCase().trim());
	}

	public boolean isInDictionary(List<Tile> chars) {
		StringBuffer str = new StringBuffer();
		for (Tile t : chars) {
			str.append(t.c);
		}
		return isInDictionary(str.toString());
	}
}

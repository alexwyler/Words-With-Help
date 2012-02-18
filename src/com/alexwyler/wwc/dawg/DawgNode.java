package com.alexwyler.wwc.dawg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.alexwyler.wwc.Tile;

public class DawgNode {

	public boolean terminal;
	private Map<Character, DawgNode> edges = new HashMap<Character, DawgNode>();

	static HashMap<File, DawgNode> instances = new HashMap<File, DawgNode>();

	public static DawgNode getInstance(File file) {
		DawgNode instance = instances.get(file);
		if (instance == null) {
			try {
				instance = makeDawg(file);
				instances.put(file, instance);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	private static DawgNode makeDawg(File dict) throws FileNotFoundException {
		DawgNode root = new DawgNode();
		Scanner in = new Scanner(dict);
		while (in.hasNextLine()) {
			DawgNode cur = root;
			for (char c : in.nextLine().toCharArray()) {
				DawgNode next = cur.edges.get(c);
				if (next == null) {
					next = new DawgNode();
					cur.edges.put(Character.toLowerCase(c), next);
				}
				cur = next;
			}
			cur.terminal = true;
		}
		return root;
	}

	public DawgNode getChild(Tile t) {
		return getChild(t.c);
	}

	public DawgNode getChild(char c) {
		return edges.get(Character.toLowerCase(c));
	}

}

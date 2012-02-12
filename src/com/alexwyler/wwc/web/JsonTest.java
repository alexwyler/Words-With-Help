package com.alexwyler.wwc.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

public class JsonTest {

	@Test
	public void testShit() {
		Map<String, Object> mainResp = new HashMap<String, Object>();
		List<Map<String, Object>> plays = new ArrayList<Map<String, Object>>();
		Map<String, Object> playInfo = new HashMap<String, Object>();
		List<Map<String, Object>> moves = new ArrayList<Map<String, Object>>();
		Map<String, Object> move1 = new HashMap<String, Object>();
		move1.put("x", 1);
		move1.put("y", 2);
		move1.put("letter", 'C');
		Map<String, Object> move2 = new HashMap<String, Object>();
		move2.put("x", 1);
		move2.put("y", 3);
		move2.put("letter", 'A');
		Map<String, Object> move3 = new HashMap<String, Object>();
		move3.put("x", 1);
		move3.put("y", 4);
		move3.put("letter", 'T');

		moves.add(move1);
		moves.add(move2);
		moves.add(move3);
		playInfo.put("plays", moves);
		playInfo.put("score", 31);
		plays.add(playInfo);
		mainResp.put("options", plays);
		System.out.println(new JSONObject(mainResp));
	}
}

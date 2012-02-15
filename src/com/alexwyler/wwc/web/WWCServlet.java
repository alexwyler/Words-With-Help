package com.alexwyler.wwc.web;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

import com.alexwyler.wwc.BoardDescription;
import com.alexwyler.wwc.Dictionary;
import com.alexwyler.wwc.GameStateException;
import com.alexwyler.wwc.PlayingBoard;
import com.alexwyler.wwc.WordsWithFriendsBoard;
import com.alexwyler.wwc.chooser.NaiveChooser;
import com.alexwyler.wwc.chooser.PlayChooser;
import com.alexwyler.wwc.chooser.PlayOption;

/**
 * Servlet implementation class WWCServlet
 */
@WebServlet("/")
public class WWCServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	JSONObject responseJSON;
	/**
	 * Default constructor.
	 */
	public WWCServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Scanner in = new Scanner(request.getInputStream());

		String input = "";
		while (in.hasNextLine()) {
			input += in.nextLine();
		}

		input = URLDecoder.decode(input, "UTF-8");
		JSONObject requestJSON = null;
		try {
			requestJSON = new JSONObject(input);
			JSONArray rack = requestJSON.getJSONArray("rack");

			List<Character> rackChars = new ArrayList<Character>();
			for (int i = 0; i < rack.length(); i++) {
				if (!"null".equals(rack.getString(i))) {
					rackChars.add(rack.getString(i).charAt(0));
				}
			}

			JSONArray board = requestJSON.getJSONArray("board");
			Character[][] existing = new Character[board.length()][board
					.length()];
			for (int x = 0; x < board.length(); x++) {
				JSONArray boardY = board.getJSONArray(x);
				for (int y = 0; y < boardY.length(); y++) {
					String letter = boardY.getString(y);
					if ("null".equals(letter)) {
						existing[x][y] = null;
					} else {
						existing[x][y] = letter.charAt(0);
					}
				}
			}

			File dictFile = new File(request.getSession().getServletContext()
					.getRealPath("words.txt"));

			Dictionary dict = Dictionary.getInstance(dictFile);
			BoardDescription boardDesc = new WordsWithFriendsBoard();
			PlayingBoard game = new PlayingBoard(boardDesc, dict, existing);
			PlayChooser chooser = new NaiveChooser(game, rackChars);
			List<PlayOption> options = chooser.getOptions();

			responseJSON = encodeOptions(options);

		} catch (JSONException e) {
			e.printStackTrace();
			invalidInput();
		} catch (RuntimeException e) {
			e.printStackTrace();
			internalError();
		} catch (GameStateException e) {
			e.printStackTrace();
			internalError();
		}

		PrintWriter out = response.getWriter();
		out.append(responseJSON.toString());
	}

	private void internalError() {
		responseJSON = new JSONObject();
		try {
			responseJSON.put("error", "Server Error");
		} catch (JSONException e) {
			e.printStackTrace();
			// fucked if this happens
		}
	}

	private void invalidInput() {
		responseJSON = new JSONObject();
		try {
			responseJSON.put("error", "Bad data from client");
		} catch (JSONException e) {
			// fucked if this happens
		}
	}

	public JSONObject encodeOptions(List<PlayOption> options) {
		Map<String, Object> mainResp = new HashMap<String, Object>();
		List<Map<String, Object>> plays = new ArrayList<Map<String, Object>>();

		for (PlayOption option : options) {
			Map<String, Object> playInfo = new HashMap<String, Object>();
			List<Map<String, Object>> moves = new ArrayList<Map<String, Object>>();
			for (Point point : option.getMove().keySet()) {
				Character c = option.getMove().get(point);
				Map<String, Object> move = new HashMap<String, Object>();
				move.put("x", point.x);
				move.put("y", point.y);
				move.put("letter", c);
				moves.add(move);
			}
			playInfo.put("plays", moves);
			playInfo.put("score", option.getScore());
			plays.add(playInfo);
		}
		mainResp.put("options", plays);
		JSONObject jsonResponse = new JSONObject(mainResp);
		return jsonResponse;
	}

}

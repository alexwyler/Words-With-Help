package com.alexwyler.wwc.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import com.alexwyler.wwc.InvalidPlayException;
import com.alexwyler.wwc.PlayingBoard;
import com.alexwyler.wwc.Point;
import com.alexwyler.wwc.Tile;
import com.alexwyler.wwc.WordsWithFriendsBoard;
import com.alexwyler.wwc.chooser.DawgChooser;
import com.alexwyler.wwc.chooser.PlayChooser;
import com.alexwyler.wwc.chooser.PlayOption;
import com.alexwyler.wwc.dawg.DawgNode;

/**
 * Servlet implementation class WWCServlet
 */
@WebServlet("/")
public class WWCServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Double API_VERSION = 2.0;
	private static final String COMMAND_START_ASYNC = "async-start";
	private static final String COMMAND_START = "start";
	private static final String COMMAND_UPDATE_ASYNC = "async-update";
	private static final String PARAM_STATUS = "status";
	private static final String STATUS_MORE = "more";
	private static final String STATUS_DONE = "done";

	JSONObject responseJSON;

	/**
	 * Default constructor.
	 */
	public WWCServlet() {
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
	protected void doPost(final HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Scanner in = new Scanner(request.getInputStream());

		String input = "";
		while (in.hasNextLine()) {
			input += in.nextLine();
		}

		input = URLDecoder.decode(input, "UTF-8");
		JSONObject requestJSON = null;
		responseJSON = new JSONObject();
		try {
			requestJSON = new JSONObject(input);
			Double apiVersion;
			try {
				apiVersion = requestJSON.getDouble("api");
			} catch (JSONException e) {
				apiVersion = null;
			}
			if (apiVersion == null || !apiVersion.equals(API_VERSION)) {
				oldVersion();
			} else {

				File dictFile = new File(request.getSession()
						.getServletContext().getRealPath("words.txt"));
				JSONArray board = requestJSON.getJSONArray("board");
				Tile[][] existing = new Tile[board.length()][board.length()];
				for (int x = 0; x < board.length(); x++) {
					JSONArray boardY = board.getJSONArray(x);
					for (int y = 0; y < boardY.length(); y++) {
						String letter = boardY.getString(y);
						if ("null".equals(letter)) {
							existing[x][y] = null;
						} else {
							existing[x][y] = new Tile(letter.charAt(0));
						}
					}
				}

				Dictionary dict = Dictionary.getInstance(dictFile);
				BoardDescription boardDesc = new WordsWithFriendsBoard();
				PlayingBoard game = new PlayingBoard(boardDesc, dict, existing,
						1);

				String cmd = requestJSON.getString("command");
				if (COMMAND_START.equals(cmd)
						|| COMMAND_START_ASYNC.equals(cmd)) {
					JSONArray rack = requestJSON.getJSONArray("rack");

					List<Tile> rackChars = new ArrayList<Tile>();
					for (int i = 0; i < rack.length(); i++) {
						String letter = rack.getString(i);
						if (!"null".equals(letter)) {
							if ("*".equals(letter)) {
								rackChars.add(new Tile((char) 0, true));
							} else {
								rackChars.add(new Tile(letter.charAt(0)));
							}
						}
					}

					DawgNode dawg = DawgNode.getInstance(dictFile);
					PlayChooser chooser = new DawgChooser(game, rackChars, dawg);

					if (COMMAND_START.equals(cmd)) {
						Collection<PlayOption> options = new HashSet<PlayOption>(
								chooser.getOptions());
						responseJSON = encodeOptions(options, game);
					} else {
						request.getSession().setAttribute("async-error", null);
						request.getSession().setAttribute("chooser", chooser);
						request.getSession().setAttribute("last-num-sent", 0);
						request.getSession().setAttribute("game", game);
						new AsyncPlayChooserRunner(chooser).start();
					}
				}
				if (COMMAND_UPDATE_ASYNC.equals(cmd)
						|| COMMAND_START_ASYNC.equals(cmd)) {
					int lastNumSent = (Integer) request.getSession()
							.getAttribute("last-num-sent");
					PlayChooser chooser = (PlayChooser) request.getSession()
							.getAttribute("chooser");
					boolean complete = chooser.isComplete();
					List<PlayOption> options = chooser.getCurrentOptions();
					List<PlayOption> toReturn;
					synchronized (options) {
						toReturn = new ArrayList<PlayOption>(options.subList(
								lastNumSent, options.size()));
					}
					request.getSession().setAttribute("last-num-sent",
							options.size());
					responseJSON = encodeOptions(toReturn, game);
					if (complete) {
						responseJSON.put(PARAM_STATUS, STATUS_DONE);
					} else {
						responseJSON.put(PARAM_STATUS, STATUS_MORE);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			invalidInput();
		} catch (RuntimeException e) {
			e.printStackTrace();
			internalError();
		} catch (GameStateException e) {
			e.printStackTrace();
			internalError();
		} catch (InvalidPlayException e) {
			e.printStackTrace();
			internalError();
		}

		if (request.getSession().getAttribute("async-error") != null) {
			internalError();
		}
		if (responseJSON == null) {
			internalError();
		}
		String responseStr = responseJSON.toString();
		int length = responseStr.length();
		int limit = Math.min(length, 100);
		String truncResponseStr = responseStr.substring(0, limit);
		if (length > 100) {
			truncResponseStr += "...";
		}

		String remoteAddr = request.getRemoteAddr();

		System.err.println(remoteAddr + " - " + truncResponseStr);
		response.getWriter().append(responseStr);
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

	private void oldVersion() {
		responseJSON = new JSONObject();
		try {
			responseJSON.put("error",
					"Newer version found.  Please update to use.");
		} catch (JSONException e) {
			// fucked if this happens
		}
	}

	public JSONObject encodeOptions(Collection<PlayOption> options,
			PlayingBoard game) throws InvalidPlayException, GameStateException {
		Map<String, Object> mainResp = new HashMap<String, Object>();
		List<Map<String, Object>> plays = new ArrayList<Map<String, Object>>();
		for (PlayOption option : options) {
			Map<String, Object> playInfo = new HashMap<String, Object>();
			List<Map<String, Object>> moves = new ArrayList<Map<String, Object>>();
			List<String> createdStrings;
			synchronized (game) {
				game.placeLetters(option.getMove());
				List<List<Point>> words = game.getCreatedWords();
				createdStrings = new ArrayList<String>();
				for (List<Point> word : words) {
					createdStrings.add(game.wordToString(word));
				}
				game.discardPending();
			}
			playInfo.put("words", createdStrings);
			for (Point point : option.getMove().getPoints()) {
				Tile t = option.getMove().getLetter(point);
				Map<String, Object> move = new HashMap<String, Object>();
				move.put("x", point.x);
				move.put("y", point.y);
				move.put("letter", t.c);
				if (t.wildcard) {
					move.put("wildcard", true);
				}
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

	class AsyncPlayChooserRunner extends Thread {

		PlayChooser chooser;

		public AsyncPlayChooserRunner(PlayChooser chooser) {
			this.chooser = chooser;
		}

		@Override
		public void run() {
			try {
				if (chooser != null) {
					chooser.getOptions();
				}
			} catch (GameStateException e) {
				e.printStackTrace();
			}
		}

	}

}

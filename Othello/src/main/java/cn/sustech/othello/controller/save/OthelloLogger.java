package cn.sustech.othello.controller.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import cn.sustech.othello.exception.IllegalChessException;
import cn.sustech.othello.exception.IllegalStepException;
import cn.sustech.othello.exception.NoPlayerException;
import cn.sustech.othello.exception.OthelloException;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;

public class OthelloLogger {
	
	private Stack<OthelloStep> logs;
	private String playerName[];
	private int useChess[];
	private Integer[] timeUpTimes;
	private boolean[] cheatModes;
	
	public OthelloLogger(File file) {
		byte[] fileContent = new byte[((Long) file.length()).intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(fileContent);
			in.close();
		} catch (FileNotFoundException e) {
			ThrowableHandler.handleThrowable(e);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
		
		this.init(new String(fileContent));
		
	}
	
	public void initDefault() {
		playerName = new String[2];
		logs = new Stack<>();
		useChess = new int[2];
	}
	
	public void save(File file) throws IOException {
		JSONObject parent = new JSONObject();
		JSONObject players = new JSONObject();
		JSONObject tts = new JSONObject();
		tts.put("0", getTimeUpTimes()[0]);
		tts.put("1", getTimeUpTimes()[1]);
		parent.put("TimeUpTimes", tts);
		JSONObject cheatMode = new JSONObject();
		cheatMode.put("0", getCheatModes()[0]);
		cheatMode.put("1", getCheatModes()[1]);
		parent.put("CheatMode", cheatMode);
		for (int i = 0; i < 2; ++i) {
			JSONObject playerInf = new JSONObject();
			playerInf.put("Name", playerName[i]);
			playerInf.put("UseChess", useChess[i]);
			players.put("" + i, playerInf);
		}
		
		parent.put("Players", players);
		JSONArray array = new JSONArray();
		Iterator<OthelloStep> it = logs.iterator();
		while (it.hasNext()) {
			array.add(it.next());
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		parent.put("StepLogs", array);
		FileOutputStream out = new FileOutputStream(file);
		out.write(parent.toJSONString().getBytes());
		out.close();
	}
	
	public OthelloLogger() {
		this.initDefault();
	}
	
	public OthelloLogger(String jsonText) {
		this.init(jsonText);
	}
	
	public void setPlayerName(int side, String name) {
		playerName[side] = name;
	}
	
	public void setUseChess(int side, ChessType chess) {
		useChess[side] = chess.ordinal();
	}
	
	public String getPlayerName(int side) {
		return playerName[side];
	}
	
	public ChessType getPlayerChessType(int side) {
		if (useChess[side] <= 0 || useChess[side] > 2) {
			throw new IllegalChessException("There is an illegal chess!");
		}
		return ChessType.values()[useChess[side]];
	}
	
	private OthelloException exception;
	
	public OthelloException getException() {
		return this.exception;
	}
	
	public Integer[] getTimeUpTimes() {
		if (this.timeUpTimes == null) {
			this.timeUpTimes = new Integer[] {0, 0};
		}
		return this.timeUpTimes;
	}
	
	public boolean[] getCheatModes() {
		if (this.cheatModes == null) {
			this.cheatModes = new boolean[] {false, false};
		}
		return this.cheatModes;
	}
	
	public void setCheatModes(boolean[] cheatModes) {
		this.cheatModes = cheatModes;
	}
	
	public void setTimeUpTimes(Integer[] timeUpTimes) {
		this.timeUpTimes = timeUpTimes;
	}
	
	private void init(String jsonText) {
		exception = null;
		logs = new Stack<>();
		playerName = new String[2];
		useChess = new int[2];
		JSONObject object = JSON.parseObject(jsonText);
		JSONObject players = object.getJSONObject("Players");
		JSONObject tut = object.getJSONObject("TimeUpTimes");
		try {
			this.timeUpTimes = new Integer[] {tut.getInteger("0"), tut.getInteger("1")};
		} catch(Exception e) {
			this.timeUpTimes = new Integer[] {0, 0};
		}
		JSONObject cheatMode = object.getJSONObject("CheatMode");
		if (cheatMode != null) {
			this.cheatModes = new boolean[] {cheatMode.getBooleanValue("0"), cheatMode.getBooleanValue("1")};
		} else {
			this.cheatModes = new boolean[] {false, false};
		}
		for (int i = 0; i < 2; ++i) {
			try {
				JSONObject playerInf = players.getJSONObject("" + i);
				playerName[i] = playerInf.getString("Name");
				useChess[i] = playerInf.getIntValue("UseChess");
			} catch (Exception e) {
				exception = new NoPlayerException("There is an error while loading players!");
				return;
			}
		}
		
		ChessType lastTurn = ChessType.BLACK;
		ChessType currentTurn = ChessType.WHITE;
		ChessType board[][] = new ChessType[8][8];
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				board[i][j] = ChessType.EMPTY;
			}
		}
		board[3][3] = board[4][4] = ChessType.WHITE;
		board[3][4] = board[4][3] = ChessType.BLACK;
		for (OthelloStep step : object.getJSONArray("StepLogs").toJavaList(OthelloStep.class)) {
			currentTurn = ChessType.values()[3 - currentTurn.ordinal()];
			HashMap<Coordinate, List<Coordinate>> map = new HashMap<>();
			if (!SaveCheckUtils.hasAvailable(currentTurn, board, step.getTurn() == currentTurn.ordinal() ? step.isCheatMode() : false, map)) {
				currentTurn = ChessType.values()[3 - currentTurn.ordinal()];
				if (!SaveCheckUtils.hasAvailable(currentTurn, board, step.isCheatMode(), map)) {
					exception = new IllegalStepException("The game should had finished!");
					break;
				}
			}
			if (currentTurn != lastTurn) {
				exception = new IllegalStepException("The chess turn is wrong");
				break;
			}
			lastTurn = ChessType.values()[step.getTurn()];
			if ((board[step.getCoordinate().getX()][step.getCoordinate().getY()] != ChessType.EMPTY || !step.isCheatMode()) && map.get(step.getCoordinate()).isEmpty()) {
				exception = new IllegalStepException("There is an illegal step!");
				break;
			} else {
				for (Coordinate co : map.get(step.getCoordinate())) {
					board[co.getX()][co.getY()] = currentTurn;
				}
				board[step.getCoordinate().getX()][step.getCoordinate().getY()] = currentTurn;
			}
			for (int i = 0; i < 8; ++i) {
				for (int j = 0; j < 8; ++j) {
					int numberChess = step.getResultBoard()[i][j];
					if (numberChess < 0 || numberChess > 2) {
						exception = new IllegalChessException("There is an illegal chess in the board!");
						break;
					}
					ChessType chess = ChessType.values()[numberChess];
					if (chess != board[i][j]) {
						exception = new IllegalStepException("There is an illegal step in the board!");
						break;
					}
				}
				if (exception != null) {
					break;
				}
			}
			if (exception != null) {
				break;
			}
			logs.push(step);
			
		}
	}
	
	public OthelloStep popStep() {
		return logs.pop();
	}
	
	public OthelloStep getPeek() {
		return logs.peek();
	}
	
	public void pushStep(OthelloStep step) {
		logs.push(step);
	}
	
	public int getStepCount() {
		return this.logs.size();
	}
	
	public OthelloStep getStep(int step) {
		return this.logs.get(step);
	}
	
	public static class OthelloStep {
		
		@JSONField(name = "Step")
		private int step;
		@JSONField(name = "Coordinate")
		private Coordinate coordinate;
		@JSONField(name = "Board")
		private int resultBoard[][];
		@JSONField(name = "Turn")
		private int turn;
		@JSONField(name = "CheatMode")
		private boolean cheatMode;
		
		private OthelloStep() {}
		
		public OthelloStep(int step, Coordinate coordinate, int resultBoard[][], int turn, boolean cheatMode) {
			this.step = step;
			this.coordinate = coordinate;
			this.resultBoard = resultBoard;
			this.turn = turn;
			this.cheatMode = cheatMode;
		}

		public int getStep() {
			return step;
		}

		public void setStep(int step) {
			this.step = step;
		}

		public Coordinate getCoordinate() {
			return coordinate;
		}

		public void setCoordinate(Coordinate coordinate) {
			this.coordinate = coordinate;
		}

		public int[][] getResultBoard() {
			return resultBoard;
		}

		public int getTurn() {
			return turn;
		}

		public void setTurn(int turn) {
			this.turn = turn;
		}

		public void setResultBoard(int[][] resultBoard) {
			this.resultBoard = resultBoard;
		}
		
		public boolean isCheatMode() {
			return cheatMode;
		}

		public void setCheatMode(boolean cheatMode) {
			this.cheatMode = cheatMode;
		}

		
	}
	
}

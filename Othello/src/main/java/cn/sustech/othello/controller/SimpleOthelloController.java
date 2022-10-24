package cn.sustech.othello.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sustech.othello.OthelloTimer;
import cn.sustech.othello.OthelloTimer.OthelloTicker;
import cn.sustech.othello.controller.option.SimpleOthelloOption;
import cn.sustech.othello.controller.save.OthelloLogger;
import cn.sustech.othello.controller.save.OthelloLogger.OthelloStep;
import cn.sustech.othello.controller.save.OthelloSave;
import cn.sustech.othello.exception.IllegalBoardSizeException;
import cn.sustech.othello.exception.IllegalChessException;
import cn.sustech.othello.exception.NoPlayerException;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;
import cn.sustech.othello.model.othello.Othello;
import cn.sustech.othello.model.othello.SimpleOthello;
import cn.sustech.othello.model.player.AIPlayer;
import cn.sustech.othello.model.player.Player;
import cn.sustech.othello.view.SimpleOthelloWindow;
import cn.sustech.othello.view.Window;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;

public class SimpleOthelloController extends OthelloController implements OthelloTicker {
	
	private SimpleOthello othello;
	private SimpleOthelloWindow window;
	private boolean isStarted;
	private OthelloSave save;
	private Player players[];
	private ChessType playerSide[];
	private boolean ready[];
	private boolean myTurn;
	private int timeUpTimes[];
	private long currentRoundTime;
	
	public void loginPlayer(Player p, int side) {
		players[side] = p;
		this.save.getLogger().setPlayerName(side, p.getName());
	}
	
	public void logoutPlayer(int side) {
		ready[side] = false;
		players[side] = null;
	}
	
	public void stop() {
		isStarted = false;
	}
	
	public Player getPlayer(int side) {
		return this.players[side];
	}
	
	public ChessType getUseChess(int side) {
		return this.playerSide[side];
	}
	
	public void toggleReady(int side) {
		ready[side] = !ready[side];
		if (ready[0] && ready[1]) {
			startGame();
		}
	}
	
	public SimpleOthelloController() {
		othello = new SimpleOthello(this);
		window = new SimpleOthelloWindow(this);
		window.resetStage();
		WindowManager wm = WindowManager.getInstance();
		wm.registerWindow("simpleothello", window);
		players = new Player[2];
		ready = new boolean[2];
		playerSide = new ChessType[2];
		timeUpTimes = new int[2];
		isStarted = false;
		OthelloTimer.registerTicker("simpleothello", this);
	}
	
	@Override
	public boolean isStarted() {
		return this.isStarted;
	}
	
	@Override
	public SimpleOthello getOthello() {
		return this.othello;
	}
	
	public SimpleOthelloOption getOption() {
		return (SimpleOthelloOption) this.save.getOption();
	}
	
	public void toggleCheatMode(int side) {
		this.othello.toggleCheatMode(playerSide[side]);
		ChessType zero = this.playerSide[0];
		this.save.getLogger().setCheatModes(new boolean[] {this.othello.isCheatModeActive(zero), this.othello.isCheatModeActive(ChessType.values()[3 - zero.ordinal()])});
		try {
			SaveController.getController().save(this.save);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
	}
	
	public void resetGame() {
		this.othello.resetGame();
		this.othello.setDefaultGame();
		isStarted = false;
		ready = new boolean[2];
		timeUpTimes = new int[2];
		playerSide = new ChessType[2];
		SimpleOthelloOption option = (SimpleOthelloOption) this.save.getOption();
		if (option.hasTimeLimit()) {
			this.timeUpTimes = new int[] {option.getTimeUpTiems(), option.getTimeUpTiems()};
		} else {
			this.timeUpTimes = new int[] {0, 0};
		}
		while (this.save.getLogger().getStepCount() > 0) {
			this.save.getLogger().popStep();
		}
		try {
			SaveController.getController().save(this.save);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
	}
	
	public void startGame() {
		isStarted = true;
		SimpleOthelloOption option = (SimpleOthelloOption) this.save.getOption();
		if (this.playerSide[0] == null) {
			int random = (int) Math.round(Math.random());
			this.playerSide[0] = ChessType.values()[random + 1];
			this.playerSide[1] = ChessType.values()[3 - this.playerSide[0].ordinal()];
			for (int i = 0; i < 2; ++i) {
				this.save.getLogger().setPlayerName(i, players[i].getName());
				this.save.getLogger().setUseChess(i, this.playerSide[i]);
			}
			try {
				SaveController.getController().save(this.save);
			} catch (IOException e) {
				ThrowableHandler.handleThrowable(e);
			}
		}
		this.window.startGame(this.playerSide.clone());
		this.othello.searchForAllowableCoordinate();
		this.window.updateTimeUpTimes(0, this.timeUpTimes[0]);
		this.window.updateTimeUpTimes(1, this.timeUpTimes[1]);
		changeSide(true);
		
	}
	
	public void forceEndGame() {
		isStarted = false;
		this.othello.endGame();
	}
	
	public void onEndGame(ChessType type) {
		isStarted = false;
		if (type != ChessType.EMPTY) {
			Player winner = playerSide[0] == type ? players[0] : players[1];
			players[0].setTotalRounds(players[0].getTotalRounds() + 1);
			players[1].setTotalRounds(players[1].getTotalRounds() + 1);
			winner.setWinRounds(winner.getWinRounds() + 1);
			for (int i = 0; i < 2; ++i) {
				if (!(players[i] instanceof AIPlayer)) {
					try {
						PlayerController.getController().savePlayer(players[i].getName());
					} catch (IOException e) {
						ThrowableHandler.handleThrowable(e);
					}
				}
			}
		}
		this.window.endGame(type);	
		
	}

	@Override
	public SimpleOthelloWindow getOthelloViewer() {
		return this.window;
	}
	
	private void putChess(Coordinate co) {
		boolean isCheatMode = this.othello.isCheatModeActive(this.othello.getTurn());
		this.othello.putChess(co);
		lastHover = null;
		if (!isStarted) {
			return;
		}
		int resultBoard[][] = new int[this.othello.getBoardSize()][this.othello.getBoardSize()];
		for (int i = 0; i < this.othello.getBoardSize(); ++i) {
			for (int j = 0; j < this.othello.getBoardSize(); ++j) {
				resultBoard[i][j] = this.othello.getChess(new Coordinate(i, j)).ordinal();
			}
		}
		save.getLogger().pushStep(new OthelloStep(save.getLogger().getStepCount(), co, resultBoard, this.othello.getTurn().ordinal(), isCheatMode));
		save.getLogger().setTimeUpTimes(new Integer[] {this.timeUpTimes[0], this.timeUpTimes[1]});
		try {
			SaveController.getController().save(save);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
	}
	
	public void onClicked(Coordinate co) {
		if (!isStarted) {
			return;
		}
		if (!myTurn) {
			return;
		}
		if (this.othello.isValid(co)) {
			putChess(co);
		}
		
	}
	
	private Coordinate lastHover;
	
	public void onHovered(Coordinate co) {
		if (!isStarted) {
			return;
		}
		if (!myTurn) {
			return;
		}
		if (lastHover != null) {
			this.window.removeAll(lastHover);
			if (hintList.contains(lastHover)) {
				this.window.drawHint(lastHover, this.othello.getTurn());
			}
		}
		if (!this.othello.isValid(co)) {
			return;
		}
		
		this.window.removeAll(co);
		this.window.drawHover(co, this.getOthello().getTurn());
		
		lastHover = co;
	}
	
	public void clearBoard() {
		this.window.clearBoard();
	}
	
	public void drawChess(Coordinate co, ChessType type, boolean playSound) {
		this.window.removeAll(co);
		if (playSound) {
			this.window.drawChessWithSound(type, co);
		} else {
			this.window.drawChess(type, co);
		}
	}
	
	public void flip(Coordinate co, ChessType type) {
		this.window.removeAll(co);
		this.window.drawChess(type, co);
	}
	
	public synchronized void updateLeftTime() {
		SimpleOthelloOption option = (SimpleOthelloOption) this.save.getOption();
		if (this.currentRoundTime <= 0) {
			currentRoundTime = option.getTimeLimit() * 1000;
			int side = 0;
			if (this.playerSide[1] == this.othello.getTurn()) {
				side = 1;
			}
			this.timeUpTimes[side] -= 1;
			if (this.timeUpTimes[side] == 0) {
				this.onEndGame(this.playerSide[1 - side]);
				return;
			}
			save.getLogger().setTimeUpTimes(new Integer[] {this.timeUpTimes[0], this.timeUpTimes[1]});
			try {
				SaveController.getController().save(save);
			} catch (IOException e) {
				ThrowableHandler.handleThrowable(e);
			}
			this.window.updateTimeUpTimes(side, this.timeUpTimes[side]);
		}
		this.window.updateProgressBar((double) currentRoundTime / (option.getTimeLimit() * 1000));
	}
	
	private int currentStepTime = 0;
	
	public void changeSide(boolean hasPutChess) {
		++currentStepTime;
		if (!isStarted) {
			return;
		}
		SimpleOthelloOption option = (SimpleOthelloOption) this.save.getOption();
		if (option.hasTimeLimit()) {
			currentRoundTime = option.getTimeLimit() * 1000;
			this.updateLeftTime();
		}
		
		Player currentPlayer = players[0];
		if (playerSide[1] == this.othello.getTurn()) {
			currentPlayer = players[1];
		}
		
		if (currentPlayer instanceof AIPlayer) {
			myTurn = false;
			final Player finalPlayer = currentPlayer;
			final int stepTime = currentStepTime;
			OthelloTimer.runTaskLater(() -> {
				Platform.runLater(() -> {
					if (!isStarted) {
						return;
					}
					if (currentStepTime != stepTime) {
						return;
					}
					List<Coordinate> list = new ArrayList<>();
					ChessType board[][] = new ChessType[this.othello.getBoardSize()][this.othello.getBoardSize()];
					for (int i = 0; i < this.othello.getBoardSize(); ++i) {
						for (int j = 0; j < this.othello.getBoardSize(); ++j) {
							Coordinate co = new Coordinate(i, j);
							board[i][j] = this.othello.getChess(co);
							if (this.othello.isValid(co)) {
								list.add(co);
							}
						}
					}
					putChess(((AIPlayer) finalPlayer).getNextStep(list, board));
				});
			}, 4);
		} else {
			myTurn = true;
		}
	}
	
	public void cancelChess() {
		
		if ((players[0] instanceof AIPlayer) && (players[1] instanceof AIPlayer)) {
			return;
		}
		
		if (this.save.getLogger().getStepCount() == 0) {
			return;
		}
		this.save.getLogger().popStep();
		if (this.save.getLogger().getStepCount() > 0) {
			this.othello.setBoard(this.save.getLogger().getPeek().getResultBoard());
			this.othello.setTurn(ChessType.values()[3 - this.save.getLogger().getPeek().getTurn()]);
		} else {
			this.othello.setDefaultGame();
			this.othello.setTurn(ChessType.WHITE);
		}
		
		Player p = players[0];
		if (playerSide[0] == this.othello.getTurn()) {
			p = players[1];
		}
		
		if ((p instanceof AIPlayer) && this.save.getLogger().getStepCount() > 0) {
			this.cancelChess();
			return;
		}
		
		this.othello.changeSide(true);
		try {
			SaveController.getController().save(this.save);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
		this.othello.searchForAllowableCoordinate();
	}
	
	public void initSave(OthelloSave save) {
		this.save = save;
		this.window.setTitle("存档 - " + save.getName());
		OthelloLogger logger = save.getLogger();
		if (this.save.getOption().getOption("BoardSize") == null || (Integer) this.save.getOption().getOption("BoardSize") != 8) {
			throw new IllegalBoardSizeException("The board should be 8x8!");
		}
		if (logger.getException() != null) {
			throw logger.getException();
		}
		SimpleOthelloOption option = (SimpleOthelloOption) this.save.getOption();
		if (logger.getStepCount() != 0) {
			
			this.othello.resetGame();
			int numberTurn = logger.getPeek().getTurn();
			if (numberTurn <= 0 || numberTurn > 2) {
				throw new IllegalChessException("There is an illegal chess turn!");
			}
			this.othello.setTurn(ChessType.values()[numberTurn]);
			
			this.othello.setBoard(logger.getPeek().getResultBoard());
			if (option.hasTimeLimit()) {
				Integer tut[] = logger.getTimeUpTimes();
				this.timeUpTimes = new int[] {tut[0], tut[1]};
			} else {
				this.timeUpTimes = new int[] {0, 0};
			}
			
			for (int i = 0; i < 2; ++i) {
				String playerName = this.save.getLogger().getPlayerName(i);
				if (playerName == null || PlayerController.getController().getPlayer(playerName) == null) {
					throw new NoPlayerException("There is no player in this started game!");
				}
				this.players[i] = PlayerController.getController().getPlayer(playerName);
				this.playerSide[i] = logger.getPlayerChessType(i);
				this.window.initPlayerInformation(i, this.players[i]);
			}
			if (this.playerSide[0] == this.playerSide[1]) {
				throw new IllegalChessException("The two player can not use the same chess!");
			}
			if (option.isCheatMode()) {
				if (logger.getCheatModes()[0]) {
					this.othello.initCheatMode(playerSide[0], true);
					this.window.setCheatModeStatus(0, true);
				}
				if (logger.getCheatModes()[1]) {
					this.othello.initCheatMode(playerSide[1], true);
					this.window.setCheatModeStatus(1, true);
				}
			}
			
		} else {
			this.othello.resetGame();
			this.othello.setDefaultGame();
			if (option.hasTimeLimit()) {
				this.timeUpTimes = new int[] {option.getTimeUpTiems(), option.getTimeUpTiems()};
			} else {
				this.timeUpTimes = new int[] {0, 0};
			}
		}
	}
	
	private List<Coordinate> hintList;
	
	public void updateAllowableCoordinate() {
		if (hintList != null) {
			hintList.forEach(co -> {
				if (this.othello.getChess(co) == ChessType.EMPTY) {
					this.window.removeAll(co);
				}
			});
		}
		hintList = new ArrayList<>();
		for (int i = 0; i < this.othello.getBoardSize(); ++i) {
			for (int j = 0; j < this.othello.getBoardSize(); ++j) {
				Coordinate co = new Coordinate(i, j);
				if (this.othello.isValid(co)) {
					hintList.add(co);
					this.window.drawHint(co, this.othello.getTurn());
				}
			}
		}
	}
	
	private long ticks = 0;
	
	@Override
	public void tick() {
		Platform.runLater(() -> {
			if (!isStarted) {
				return;
			}
			SimpleOthelloOption option = (SimpleOthelloOption) this.save.getOption();
			if (!option.hasTimeLimit()) {
				return;
			}
			this.currentRoundTime -= OthelloTimer.INTERVAL;
			this.updateLeftTime();
		});
	}

	public void updateScore() {
		int score[] = new int[2];
		score[0] = score[1] = 0;
		for (int i = 0; i < this.othello.getBoardSize(); ++i) {
			for (int j = 0; j < this.othello.getBoardSize(); ++j) {
				ChessType chess = this.othello.getChess(new Coordinate(i, j));
				if (chess != ChessType.EMPTY) {
					if (playerSide[0] == chess) {
						++score[0];
					} else {
						++score[1];
					}
				}
			}
		}
		this.window.updateScore(score);
	}
	

}

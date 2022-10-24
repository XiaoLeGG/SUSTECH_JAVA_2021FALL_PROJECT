package cn.sustech.othello.model.othello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sustech.othello.controller.SimpleOthelloController;
import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;
import cn.sustech.othello.model.player.Player;

public class SimpleOthello extends Othello {
	
	
	private static final int BOARD_SIZE = 8;
	private ChessType board[][];
	private ChessType turn;
	private SimpleOthelloController controller;
	private HashMap<Coordinate, List<Coordinate>> flipCoordinates;
	private boolean cheatMode[];
	
	public SimpleOthello(SimpleOthelloController controller) {
		this.controller = controller;
	}
	
	private boolean lastHasChess;
	
	public void resetGame() {
		lastHasChess = true;
		cheatMode = new boolean[3];
		turn = ChessType.BLACK;
		board = new ChessType[BOARD_SIZE][BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				board[i][j] = ChessType.EMPTY;
			}
		}
	}
	
	public void setDefaultGame() {
		this.controller.clearBoard();
		lastHasChess = true;
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				board[i][j] = ChessType.EMPTY;
			}
		}
		
		board[3][3] = board[4][4] = ChessType.WHITE;
		board[3][4] = board[4][3] = ChessType.BLACK;
		this.controller.drawChess(new Coordinate(3, 3), ChessType.WHITE, false);
		this.controller.drawChess(new Coordinate(4, 4), ChessType.WHITE, false);
		this.controller.drawChess(new Coordinate(3, 4), ChessType.BLACK, false);
		this.controller.drawChess(new Coordinate(4, 3), ChessType.BLACK, false);
	}
	
	public void setBoard(int board[][]) {
		this.lastHasChess = true;
		this.controller.clearBoard();
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				this.board[i][j] = ChessType.values()[board[i][j]];
				this.controller.drawChess(new Coordinate(i, j), this.board[i][j], false);
			}
		}
	}
	
	public void setTurn(ChessType turn) {
		this.turn = turn;
	}
	
	public boolean isValid(Coordinate co) {
		if (cheatMode[this.turn.ordinal()]) {
			if (board[co.getX()][co.getY()] == ChessType.EMPTY) {
				return true;
			}
		}
		return flipCoordinates.get(co).size() > 0;
	}
	
	private static final int DIRECTION[][] = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
	
	public ChessType endGame() {
		int cnt[] = new int[3];
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				++cnt[board[i][j].ordinal()];
			}
		}
		if (cnt[ChessType.BLACK.ordinal()] > cnt[ChessType.WHITE.ordinal()]) {
			this.controller.onEndGame(ChessType.BLACK);
			return ChessType.BLACK;
		}
		if (cnt[ChessType.BLACK.ordinal()] < cnt[ChessType.WHITE.ordinal()]) {
			this.controller.onEndGame(ChessType.WHITE);
			return ChessType.WHITE;
		}
		this.controller.onEndGame(ChessType.EMPTY);
		return ChessType.EMPTY;
	}
	
	private boolean hasChess =  true;
	public void searchForAllowableCoordinate() {
		lastHasChess = hasChess;
		hasChess = false;
		flipCoordinates = new HashMap<>();
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				Coordinate co = new Coordinate(i, j);
				this.searchCoordinate(co);
				if (isValid(co)) {
					hasChess = true;
					
				}
			}
		}
		this.controller.updateScore();
		this.controller.updateAllowableCoordinate();
		if (!hasChess) {
			if (lastHasChess == false) {
				this.endGame();
				return;
			}
			this.changeSide(false);
		}
	}
	
	public void changeSide(boolean hasChess) {
		this.turn = ChessType.values()[3 - this.turn.ordinal()];
		this.controller.changeSide(hasChess);
		this.searchForAllowableCoordinate();
	}
	
	public void initCheatMode(ChessType side, boolean status) {
		cheatMode[side.ordinal()] = status;
	}
	
	public void toggleCheatMode(ChessType side) {
		cheatMode[side.ordinal()] = !cheatMode[side.ordinal()];
		if (this.turn == side) {
			this.searchForAllowableCoordinate();
		}
	}
	
	public boolean isCheatModeActive(ChessType side) {
		return this.cheatMode[side.ordinal()];
	}
	
	public boolean putChess(Coordinate co) {
		if (!this.controller.isStarted()) {
			return false;
		}
		int x = co.getX();
		int y = co.getY();
		if (!isValid(co)) {
			return false;
		}
		this.board[x][y] = this.turn;
		this.controller.drawChess(co, this.turn, true);
		this.flipCoordinates.get(co).forEach(flip -> {
			this.board[flip.getX()][flip.getY()] = this.turn;
			this.controller.flip(flip, this.turn);
		});
		
		this.changeSide(true);
		return true;
	}
	
	private boolean searchCoordinate(Coordinate co) {
		flipCoordinates.put(co, new ArrayList<>());
		int x = co.getX();
		int y = co.getY();
		boolean flag = false;
		if (board[x][y] != ChessType.EMPTY) {
			return false;
		}
		for (int current = 0; current < 8; ++current) {
			int nx = x + DIRECTION[current][0];
			int ny = y + DIRECTION[current][1];
			if (nx < 0 || nx >= BOARD_SIZE || ny < 0 || ny >= BOARD_SIZE || !board[nx][ny].opposite(this.turn)) {
				continue;
			}
			for (nx += DIRECTION[current][0], ny += DIRECTION[current][1]; nx >= 0 && nx < BOARD_SIZE && ny >= 0 && ny < BOARD_SIZE; nx += DIRECTION[current][0], ny += DIRECTION[current][1]) {
				if (board[nx][ny] == this.turn) {
					for (int bx = nx - DIRECTION[current][0], by = ny - DIRECTION[current][1]; board[bx][by] != ChessType.EMPTY; bx -= DIRECTION[current][0], by -= DIRECTION[current][1]) {
						flipCoordinates.get(co).add(new Coordinate(bx, by));
						flag = true;
					}
					break;
				}
			}
		}
		
		return flag;
		
	}

	@Override
	public ChessType getChess(Coordinate coordinate) {
		return this.board[coordinate.getX()][coordinate.getY()];
	}


	@Override
	public String getName() {
		return "SimpleOthello";
	}

	@Override
	public ChessType getTurn() {
		return this.turn;
	}

	@Override
	public int getBoardSize() {
		return BOARD_SIZE;
	}
	
}

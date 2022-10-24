package cn.sustech.othello.model.othello;

import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;
import cn.sustech.othello.model.player.Player;

public abstract class Othello {
	
	public abstract ChessType getChess(Coordinate coordinate);
	public abstract ChessType getTurn();
	public abstract String getName();
	public abstract int getBoardSize();
	
}

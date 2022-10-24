package cn.sustech.othello.model.player;

import java.util.List;

import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;

public abstract class AIPlayer extends Player {
	
	public abstract Coordinate getNextStep(List<Coordinate> possibleChess, ChessType board[][]);
	
}

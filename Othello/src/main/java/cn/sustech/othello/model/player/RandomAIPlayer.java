package cn.sustech.othello.model.player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;

public class RandomAIPlayer extends AIPlayer{
	
	@Override
	public Coordinate getNextStep(List<Coordinate> possibleChess, ChessType[][] board) {
		Random random = new Random();
		int n = random.nextInt(possibleChess.size());
		return possibleChess.get(n);
	}

	@Override
	public String getName() {
		return "\\可爱的AI/";
	}

	@Override
	public int getTotalRounds() {
		return 0;
	}

	@Override
	public int getWinRounds() {
		return 0;
	}

	@Override
	public UUID getUUID() {
		return UUID.randomUUID();
	}

	@Override
	public void setTotalRounds(int rounds) {}

	@Override
	public void setWinRounds(int rounds) {}
	
}

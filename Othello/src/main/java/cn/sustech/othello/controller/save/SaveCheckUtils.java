package cn.sustech.othello.controller.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;

public final class SaveCheckUtils {
	private SaveCheckUtils() { throw new NullPointerException("You can not construct this class!");}
	
	private static final int DIRECTION[][] = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
	
	public static boolean hasAvailable(ChessType turn, ChessType board[][], boolean isCheatMode, HashMap<Coordinate, List<Coordinate>> hashMap) {
		boolean flag = false;
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				if (board[i][j] != ChessType.EMPTY) {
					continue;
				}
				List<Coordinate> flips = new ArrayList<>();
				Coordinate pos = new Coordinate(i, j);
				
				if (searchCoordinate(turn, pos, board, flips) || (isCheatMode && board[pos.getX()][pos.getY()] == ChessType.EMPTY)) {
					flag = true;
				}
				hashMap.put(pos, flips);
			}
		}
		return flag;
	}
	
	private static boolean searchCoordinate(ChessType turn, Coordinate co, ChessType board[][],  List<Coordinate> flipCoordinates) {
		int x = co.getX();
		int y = co.getY();
		boolean flag = false;
		if (board[x][y] != ChessType.EMPTY) {
			return false;
		}
		for (int current = 0; current < 8; ++current) {
			int nx = x + DIRECTION[current][0];
			int ny = y + DIRECTION[current][1];
			if (nx < 0 || nx >= 8 || ny < 0 || ny >= 8 || !board[nx][ny].opposite(turn)) {
				continue;
			}
			for (nx += DIRECTION[current][0], ny += DIRECTION[current][1]; nx >= 0 && nx < 8 && ny >= 0 && ny < 8; nx += DIRECTION[current][0], ny += DIRECTION[current][1]) {
				if (board[nx][ny] == turn) {
					for (int bx = nx - DIRECTION[current][0], by = ny - DIRECTION[current][1]; board[bx][by] != ChessType.EMPTY; bx -= DIRECTION[current][0], by -= DIRECTION[current][1]) {
						flipCoordinates.add(new Coordinate(bx, by));
						flag = true;
					}
					break;
				}
			}
		}
		return flag;
		
	}
	
}

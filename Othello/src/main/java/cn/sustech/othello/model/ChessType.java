package cn.sustech.othello.model;

public enum ChessType {
	EMPTY, BLACK, WHITE;
	
	public boolean opposite(ChessType type) {
		if (this == ChessType.EMPTY) {
			return false;
		}
		return this.ordinal() == 3 - type.ordinal();
	}
	
}

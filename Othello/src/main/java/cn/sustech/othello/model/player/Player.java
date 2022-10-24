package cn.sustech.othello.model.player;

import java.util.UUID;

public abstract class Player {
	
	public abstract String getName();
	public abstract int getTotalRounds();
	public abstract int getWinRounds();
	public abstract UUID getUUID();
	public abstract void setTotalRounds(int rounds);
	public abstract void setWinRounds(int rounds);
	
}

package cn.sustech.othello.model.player;

import java.util.UUID;

import com.alibaba.fastjson.annotation.JSONField;

public class SimplePlayer extends Player {
	
	@JSONField(name = "Name")
	private String name;
	@JSONField(name = "UUID")
	private UUID uuid;
	@JSONField(name = "WinRounds")
	private int winRounds;
	@JSONField(name = "TotalRounds")
	private int totalRounds;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	private SimplePlayer() {}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public UUID getUUID() {
		return this.uuid;
	}

	@Override
	public int getTotalRounds() {
		return this.totalRounds;
	}

	@Override
	public int getWinRounds() {
		return this.winRounds;
	}
	
	public static SimplePlayer generateNewPlayer(String name) {
		SimplePlayer player = new SimplePlayer();
		player.setUUID(UUID.randomUUID());
		player.setName(name);
		player.setTotalRounds(0);
		player.setWinRounds(0);
		return player;
	}

	@Override
	public void setWinRounds(int winRounds) {
		this.winRounds = winRounds;
	}
	
	@Override
	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

}

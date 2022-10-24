package cn.sustech.othello.controller.packet;

import com.alibaba.fastjson.JSON;

import cn.sustech.othello.model.player.Player;
import cn.sustech.othello.model.player.SimplePlayer;

public class PlayerInformationPacket extends Packet {
	
	private String context;
	
	public PlayerInformationPacket(SimplePlayer p) {
		this.context = JSON.toJSONString(p);
	}
	
	public PlayerInformationPacket(String context) {
		this.context = context;
	}
	
	@Override
	public String getContext() {	
		return this.context;
	}
	
	public Player getPlayer() {
		return JSON.parseObject(this.context, SimplePlayer.class);
	}
	
	@Override
	public int getCode() {
		return PlayerInformationPacket.getStaticCode();
	}

	public static int getStaticCode() {
		return 1;
	}
	
	
	
}

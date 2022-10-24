package cn.sustech.othello.controller.packet;

import com.alibaba.fastjson.JSON;

import cn.sustech.othello.model.Coordinate;

public class PlayerPutChessPacket extends Packet {

	private String context;
	
	public PlayerPutChessPacket(Coordinate co) {
		this.context = JSON.toJSONString(co);
	}
	
	public PlayerPutChessPacket(String context) {
		this.context = context;
	}
	
	public Coordinate getCoordinate() {
		return JSON.parseObject(context, Coordinate.class);
	}
	
	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public int getCode() {
		return PlayerPutChessPacket.getStaticCode();
	}
	
	public static int getStaticCode() {
		return 4;
	}

}

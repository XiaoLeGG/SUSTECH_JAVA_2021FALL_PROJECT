package cn.sustech.othello.controller.packet;

import cn.sustech.othello.model.ChessType;

public class StartGamePacket extends Packet {
	
	private String context;
	
	public StartGamePacket(String context) {
		this.context = context;
	}
	
	public StartGamePacket(ChessType type) {
		this.context = type.name();
	}
	
	@Override
	public String getContext() {
		return this.context;
	}
	
	public ChessType getMySide() {
		return ChessType.valueOf(this.context);
	}

	@Override
	public int getCode() {
		return StartGamePacket.getStaticCode();
	}
	
	public static int getStaticCode() {
		return 3;
	}
	
}

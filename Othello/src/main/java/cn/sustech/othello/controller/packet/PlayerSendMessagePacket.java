package cn.sustech.othello.controller.packet;

public class PlayerSendMessagePacket extends Packet {

	private String context;
	
	public PlayerSendMessagePacket(String context) {
		this.context = context;
	}
	
	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public int getCode() {
		return PlayerSendMessagePacket.getStaticCode();
	}
	
	public static int getStaticCode() {
		return 7;
	}

}

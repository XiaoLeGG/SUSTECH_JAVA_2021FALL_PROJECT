package cn.sustech.othello.controller.packet;

public class PlayerToggleReadyPacket extends Packet {
	
	private String context;
	
	public PlayerToggleReadyPacket(String context) {
		this.context = context;
	}
	
	public PlayerToggleReadyPacket() {
		this.context = "ready";
	}
	
	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public int getCode() {
		return PlayerToggleReadyPacket.getStaticCode();
	}
	
	public static int getStaticCode() {
		return 2;
	}

}

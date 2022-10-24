package cn.sustech.othello.controller.packet;

public class HeartBeatPacket extends Packet {
	
	public HeartBeatPacket(String s) {} 
	
	public HeartBeatPacket() {}

	@Override
	public String getContext() {
		return "❤";
	}

	@Override
	public int getCode() {
		return HeartBeatPacket.getStaticCode();
	}
	
	public static int getStaticCode() {
		return 0;
	}

}

package cn.sustech.othello.controller.packet;

public class FarewellPacket extends Packet {

	public FarewellPacket() {}
	public FarewellPacket(String s) {}
	
	@Override
	public String getContext() {
		return "bye";
	}

	@Override
	public int getCode() {
		return FarewellPacket.getStaticCode();
	}
	
	public static int getStaticCode() {
		return 5;
	}
	
}

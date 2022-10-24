package cn.sustech.othello.controller.packet;

public class TransferProfilePacket extends Packet {
	
	private String context;
	
	public TransferProfilePacket(String context) {
		this.context = context;
	}
	
	@Override
	public String getContext() {
		return this.context;
	}
	

	@Override
	public int getCode() {
		return TransferProfilePacket.getStaticCode();
	}
	
	public static int getStaticCode() {
		return 8;
	}

}

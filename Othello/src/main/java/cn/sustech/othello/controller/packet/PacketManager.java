package cn.sustech.othello.controller.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import cn.sustech.othello.exception.ThrowableHandler;

public class PacketManager {
	
	private static PacketManager manager = new PacketManager();
	private HashMap<Integer, Class<? extends Packet>> packetCodes;
	
	private PacketManager() {
		this.init();
	}
	
	private void init() {
		packetCodes = new HashMap<>();
		packetCodes.put(HeartBeatPacket.getStaticCode(), HeartBeatPacket.class);
		packetCodes.put(PlayerInformationPacket.getStaticCode(), PlayerInformationPacket.class);
		packetCodes.put(PlayerToggleReadyPacket.getStaticCode(), PlayerToggleReadyPacket.class);
		packetCodes.put(StartGamePacket.getStaticCode(), StartGamePacket.class);
		packetCodes.put(PlayerPutChessPacket.getStaticCode(), PlayerPutChessPacket.class);
		packetCodes.put(FarewellPacket.getStaticCode(), FarewellPacket.class);
		packetCodes.put(PlayerSendMessagePacket.getStaticCode(), PlayerSendMessagePacket.class);
		packetCodes.put(TransferProfilePacket.getStaticCode(), TransferProfilePacket.class);
	}
	
	public static PacketManager getInstance() {
		return manager;
	}
	
	public Packet receivePacket(int len, byte[] packetBytes) {
		String msg = new String(packetBytes, 0, len);
		int index = msg.indexOf('@');
		if (index == -1) {
			return null;
		}
		int code = Integer.parseInt(msg.substring(0, index));
		String context = msg.substring(index + 1);
		Class<? extends Packet> packetClazz = packetCodes.get(code);
		try {
			Constructor<? extends Packet> constructor = packetClazz.getConstructor(String.class);
			return constructor.newInstance(context);
		} catch (Exception e) {
			ThrowableHandler.handleThrowable(e);
		}
		return null;
	}
	
}

package cn.sustech.othello.controller;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import cn.sustech.othello.CacheManager;
import cn.sustech.othello.controller.ReceiveThread.Receiver;
import cn.sustech.othello.controller.packet.FarewellPacket;
import cn.sustech.othello.controller.packet.HeartBeatPacket;
import cn.sustech.othello.controller.packet.Packet;
import cn.sustech.othello.controller.packet.PacketManager;
import cn.sustech.othello.exception.ThrowableHandler;
import javafx.application.Platform;

public class ClientThread extends Thread implements Receiver {
	
	private String ip;
	private OnlineOthelloController controller;
	private Socket socket;
	private ReceiveThread receiveThread;
	private BufferedOutputStream writer;
	private boolean isStop;
	private long lastBeat;
	
	public ClientThread(OnlineOthelloController controller, String ip) {
		this.controller = controller;
		this.ip = ip;
		isStop = false;
	}
	
	public boolean isConnected() {
		return this.socket != null && this.socket.isConnected();
	}
	
	public boolean connect() {
		try {
			socket = new Socket();
			socket.setSoTimeout(3000);
			socket.setKeepAlive(true);
			socket.setOOBInline(true);
			socket.connect(new InetSocketAddress(this.ip, 8888));;
			if (socket.isConnected()) {
				socket.setSoTimeout(1000000);
			}
			return socket.isConnected();
		} catch (Exception e) {
			return false;
		}
	}
	
	public void sendPacket(Packet packet) {
		try {
			writer.write((packet.getCode() + "@" + packet.getContext()).getBytes());
			writer.flush();
		} catch (Exception e) {
			this.onClose();
		}
	}
	
	public void setStop(boolean sendPacket) {
		if (isStop) {
			return;
		}
		this.isStop = true;
		try {
			if (sendPacket) {
				try {
					this.sendPacket(new FarewellPacket());
				} catch (Exception e1) {}
			}
			this.receiveThread.setStop(true);
			writer.close();
			if (!this.socket.isClosed()) {
				this.socket.close();
			}
		} catch(Exception e) {}
	}
	
	@Override
	public void run() {
		try {
			writer = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
		receiveThread = new ReceiveThread(this);
		receiveThread.setDaemon(true);
		receiveThread.start();
		lastBeat = System.currentTimeMillis();
		while (!isStop) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.sendPacket(new HeartBeatPacket());
			if (System.currentTimeMillis() - lastBeat > 6000) {
				this.onClose();
			}
		}
	}
		
	@Override
	public void onReceive(int len, byte[] bytes) {
		this.controller.receivePacket(PacketManager.getInstance().receivePacket(len, bytes));
	}

	@Override
	public void onClose() {
		if (isStop) {
			return;
		}
		Platform.runLater(() -> {
			try {
				isStop = true;
				this.receiveThread.setStop(true);
				writer.close();
				if (!this.socket.isClosed()) {
					this.socket.close();
				}
			} catch(Exception e) {}
			this.controller.logoutOnlinePlayer();
		});
	}

	@Override
	public InputStream getInputStream() {
		try {
			return socket.getInputStream();
		} catch (IOException e) {
			onClose();
		}
		return null;
	}
	
	private File currentTransferFile;
	private String fileName;
	public void setTransferFile(File file, String fileName) {
		this.currentTransferFile = file;
		this.fileName = fileName;
	}
	
	public void startTransferTask() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Socket socket = new Socket();
				
				try {
					socket.connect(new InetSocketAddress(ip, 8889));
					socket.setSoTimeout(3000);
					
					try {
						DataInputStream input = new DataInputStream(socket.getInputStream());
						String fileName = input.readUTF();
						if (fileName != null) {
							CacheManager.getManager().saveCache(fileName, input);
						}
					} catch(Throwable e) {}
					
					if (currentTransferFile != null) {
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						FileInputStream in = new FileInputStream(currentTransferFile);
						out.writeUTF(fileName == null ? currentTransferFile.getName() : fileName);
						out.flush();
						int len = 0;
						byte[] datas = new byte[1024 * 8];
						while ((len = in.read(datas)) != -1) {
							out.write(datas, 0, len);
						}
						in.close();
						out.flush();
						out.close();
						currentTransferFile = null;
					}
					socket.close();
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	protected void beat() {
		lastBeat = System.currentTimeMillis();
	}

}

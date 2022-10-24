package cn.sustech.othello.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cn.sustech.othello.CacheManager;
import cn.sustech.othello.controller.ReceiveThread.Receiver;
import cn.sustech.othello.controller.packet.FarewellPacket;
import cn.sustech.othello.controller.packet.HeartBeatPacket;
import cn.sustech.othello.controller.packet.Packet;
import cn.sustech.othello.controller.packet.PacketManager;
import cn.sustech.othello.controller.packet.PlayerInformationPacket;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.player.SimplePlayer;
import javafx.application.Platform;

public class ServerThread extends Thread implements Receiver{
	
	private OnlineOthelloController controller;
	private ServerSocket serverSocket;
	private ServerSocket transferServer;
	private Socket clientSocket;
	private BufferedOutputStream writer;
	private ReceiveThread receiveThread;
	private boolean isStop;
	private long lastBeat;
	private Thread transferThread;
	private File currentTransferFile;
	
	public ServerThread(OnlineOthelloController controller) {
		this.controller = controller;
		isStop = false;
	}
	
	public boolean isConnected() {
		return this.clientSocket != null && this.clientSocket.isConnected();
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket();
			transferServer = new ServerSocket();
			try {
				serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 8888));
				transferServer.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 8889));
			} catch(Exception e) {
				Platform.runLater(() -> this.controller.onRoomBinded());
				return;
			}
			Platform.runLater(() -> controller.onRoomCreated(serverSocket.getInetAddress().getHostAddress()));
			try {
				clientSocket = serverSocket.accept();
			} catch(Exception e) {
				return;
			}
			clientSocket.setKeepAlive(true);
			clientSocket.setOOBInline(true);
			writer = new BufferedOutputStream(clientSocket.getOutputStream());
			receiveThread = new ReceiveThread(this);
			receiveThread.setDaemon(true);
			receiveThread.start();
			this.sendPacket(new PlayerInformationPacket((SimplePlayer) this.controller.getPlayer(0)));
			lastBeat = System.currentTimeMillis();
			transferThread = new Thread() {
				
				@Override
				public void run() {
					while (!isStop) {
						try {
							Socket socket = transferServer.accept();
							
							try {
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
							} catch(Throwable e) {}
							
							DataInputStream input = new DataInputStream(socket.getInputStream());
							try {
								String fileName = input.readUTF();
								if (fileName != null) {
									CacheManager.getManager().saveCache(fileName, input);
								}
							} catch(Throwable e) {}
							
							
						} catch (Throwable e) {}
						
					}
				}
				
			};
			transferThread.setDaemon(true);
			transferThread.start();
			while(!isStop) {
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					ThrowableHandler.handleThrowable(e);
				}
				this.sendPacket(new HeartBeatPacket());
				if (System.currentTimeMillis() - lastBeat > 6000) {
					this.onClose();
				}
			}
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
	}
	
	public void sendPacket(Packet packet) {
		try {
			writer.write((packet.getCode() + "@" + packet.getContext()).getBytes());
			writer.flush();
		} catch (IOException e) {
			this.onClose();
		}
		
	}

	@Override
	public void onReceive(int len, byte[] bytes) {
		this.controller.receivePacket(PacketManager.getInstance().receivePacket(len, bytes));
	}
	
	public void setStop(boolean sendPacket) {
		if (isStop) {
			return;
		}
		this.isStop = true;
		try {
			if (sendPacket && this.clientSocket != null && !this.clientSocket.isClosed()) {
				try {
					this.sendPacket(new FarewellPacket());
				} catch (Exception e1) {}
			}
			if (this.receiveThread != null) {
				this.receiveThread.setStop(true);
			}
			if (this.clientSocket != null && !this.clientSocket.isClosed()) {
				this.clientSocket.close();
			}
			if (this.writer != null) {
				writer.close();
			}
			transferServer.close();
			serverSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
				if (!this.clientSocket.isClosed()) {
					this.clientSocket.close();
				}
				writer.close();
				transferServer.close();
				serverSocket.close();
			} catch(Exception e) {}
			this.controller.logoutOnlinePlayer();
		});
	}

	@Override
	public InputStream getInputStream() {
		try {
			return this.clientSocket.getInputStream();
		} catch (IOException e) {
			onClose();
		}
		return null;
	}
	
	private String fileName;
	
	public void setTransferFile(File file, String fileName) {
		this.currentTransferFile = file;
		this.fileName = fileName;
	}
	
	protected void beat() {
		lastBeat = System.currentTimeMillis();
	}
	
}

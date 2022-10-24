package cn.sustech.othello.controller;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.sustech.othello.exception.ThrowableHandler;

public class ReceiveThread extends Thread {
	
	private Receiver receiver;
	private boolean stop;
	
	public ReceiveThread(Receiver receiver) {
		this.receiver = receiver;
		stop = false;
	}
	
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	@Override
	public void run() {
			while (!stop) {
				try {
					byte[] bytes = new byte[1024 * 8];
					int len;
					while ((!stop) && (len = receiver.getInputStream().read(bytes)) != -1) {
						this.receiver.onReceive(len, bytes);
					}
				} catch (Throwable e) {
					if (this.stop) {
						return;
					}
					this.receiver.onClose();
				}
			}
		
		
	}
	
	public static interface Receiver {
		
		public void onReceive(int len, byte[] bytes);
		public void onClose();
		public InputStream getInputStream();
		
	}
	
}

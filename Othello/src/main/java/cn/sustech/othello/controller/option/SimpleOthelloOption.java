package cn.sustech.othello.controller.option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SimpleOthelloOption extends OthelloOption {
	
	private boolean cheatMode;
	private boolean hasTimeLimit;
	private long timeLimit;
	private int boardSize;
	public boolean isCheatMode() {
		return cheatMode;
	}
	public void setCheatMode(boolean cheatMode) {
		this.cheatMode = cheatMode;
	}
	public boolean hasTimeLimit() {
		return hasTimeLimit;
	}
	public void setHasTimeLimit(boolean hasTimeLimit) {
		this.hasTimeLimit = hasTimeLimit;
	}
	public long getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}
	public int getBoardSize() {
		return boardSize;
	}
	
	@Deprecated
	public void setBoardSize(int boardSize) {
		//this.boardSize = boardSize;
	}
	
	public int getTimeUpTiems() {
		return timeUpTimes;
	}
	
	public void setTimeUpTiems(int timeUpTiems) {
		this.timeUpTimes = timeUpTiems;
	}

	private int timeUpTimes;
	
	public SimpleOthelloOption() {}
	public SimpleOthelloOption(boolean hasTimeLimit, long timeLimit, int boardSize, boolean cheatMode, int timeUpTimes) {
		this.hasTimeLimit = hasTimeLimit;
		this.timeLimit = timeLimit;
		this.cheatMode = cheatMode;
		this.timeUpTimes = timeUpTimes;
		this.boardSize = boardSize;
	}
	
	@Override
	public SimpleOthelloOption loadOption(File file) throws IOException {
		if (!file.getName().toLowerCase().endsWith(".properties")) {
			throw new IllegalArgumentException("The option file should be a property file");
		}
		Properties properties = new Properties();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		properties.load(bufferedReader);
		this.hasTimeLimit = Boolean.parseBoolean(properties.getProperty("HasTimeLimit"));
		this.timeLimit = Long.parseLong(properties.getProperty("TimeLimit"));
		this.cheatMode = Boolean.parseBoolean(properties.getProperty("CheatMode"));
		this.boardSize = Integer.parseInt(properties.getProperty("BoardSize"));
		this.timeUpTimes = Integer.parseInt(properties.getProperty("TimeUpTimes"));
		bufferedReader.close();
		return this;
	}

	@Override
	@Deprecated
	public Object getOption(String key) {
		if (key.equalsIgnoreCase("HasTimeLimit")) {
			return this.hasTimeLimit;
		}
		if (key.equalsIgnoreCase("TimeLimit")) {
			return this.timeLimit;
		}
		if (key.equalsIgnoreCase("CheatMode")) {
			return this.cheatMode;
		}
		if (key.equalsIgnoreCase("BoardSize")) {
			return this.boardSize;
		}
		if (key.equalsIgnoreCase("TimeUpTimes")) {
			return this.timeUpTimes;
		}
		return null;
	}

	@Override
	@Deprecated
	public SimpleOthelloOption setOption(String key, Object object) {
		if (key.equalsIgnoreCase("HasTimeLimit")) {
			this.hasTimeLimit = (boolean) object;
		}
		if (key.equalsIgnoreCase("TimeLimit")) {
			this.timeLimit = (long) object;
		}
		if (key.equalsIgnoreCase("CheatMode")) {
			this.cheatMode = (boolean) object;
		}
		if (key.equalsIgnoreCase("BoardSize")) {
			
		}
		if (key.equalsIgnoreCase("TimeUpTimes")) {
			this.timeUpTimes = (int) object;
		}
		return this;
	}
	
	@Override
	public void save(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		Properties properties = new Properties();
		properties.setProperty("HasTimeLimit", "" + this.hasTimeLimit);
		properties.setProperty("TimeLimit", "" + this.timeLimit);
		properties.setProperty("CheatMode", "" + this.cheatMode);
		properties.setProperty("BoardSize", "" + this.boardSize);
		properties.setProperty("TimeUpTimes", "" + this.timeUpTimes);
		FileOutputStream output = new FileOutputStream(file);
		properties.store(output, "Don't change this file until neccessary!");
	}
	
}

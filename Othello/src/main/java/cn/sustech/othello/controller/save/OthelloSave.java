package cn.sustech.othello.controller.save;

import java.io.File;
import java.io.IOException;

import cn.sustech.othello.controller.option.OthelloOption;
import cn.sustech.othello.controller.option.SimpleOthelloOption;
import cn.sustech.othello.exception.SaveFileLostException;
import cn.sustech.othello.exception.ThrowableHandler;

public class OthelloSave {
	
	private static final String OTHELLO_SUFFIX = ".othello";
	private static final String OPTION_FILE_NAME = "option.properties";
	
	private String name;
	private OthelloOption option;
	private OthelloLogger logger;
	
	public OthelloSave(String name, OthelloOption option, OthelloLogger logger) {
		this.name = name;
		this.option = option;
		this.logger = logger;
	}
	
	public String getName() {
		return this.name;
	}
	
	public OthelloOption getOption() {
		return this.option;
	}
	
	public OthelloLogger getLogger() {
		return this.logger;
	}
	
	public void save(File dir) throws IOException {
		dir.mkdirs();
		File optionFile = new File(dir, OPTION_FILE_NAME);
		if (!optionFile.exists()) {
			optionFile.createNewFile();
		}
		this.option.save(optionFile);
		if (logger != null) {
			File othelloSave = new File(dir, this.name.toLowerCase() + OTHELLO_SUFFIX);
			this.logger.save(othelloSave);
		}
	}
	
	public static OthelloSave loadSave(File dir) {
		String name = dir.getName();
		File optionFile = new File(dir, OPTION_FILE_NAME);
		if (!optionFile.exists()) {
			throw new SaveFileLostException("Could not load the save" + name + ". The option file is missing!");
		}
		OthelloOption option = new SimpleOthelloOption();
		try {
			option.loadOption(optionFile);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
		File sav = new File(dir, name.toLowerCase() + OTHELLO_SUFFIX);
		OthelloLogger logger;
		if (!sav.exists()) {
			logger = new OthelloLogger();
		} else {
			logger = new OthelloLogger(sav);
		}
		return new OthelloSave(name, option, logger);
		
	}

}

package cn.sustech.othello.controller.option;

import java.io.File;
import java.io.IOException;

public abstract class OthelloOption {
	
	public abstract Object getOption(String key);
	public abstract OthelloOption setOption(String key, Object object);
	public abstract OthelloOption loadOption(File file) throws IOException;
	public abstract void save(File file) throws IOException;

	
}

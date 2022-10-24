package cn.sustech.othello.controller.option;

import java.io.File;
import java.io.IOException;

public class OnlineOthelloOption extends OthelloOption {
	
	private static OnlineOthelloOption option = new OnlineOthelloOption();
	
	private OnlineOthelloOption() {
		
	}
	
	@Override
	public Object getOption(String key) {
		if (key.equalsIgnoreCase("ischeatmode")) {
			return false;
		}
		return null;
	}

	@Override
	@Deprecated
	public OthelloOption setOption(String key, Object object) {
		return this;
	}

	@Deprecated
	@Override
	public OthelloOption loadOption(File file) throws IOException {
		return this;
	}
	
	@Override
	@Deprecated
	public void save(File file) throws IOException {
		
	}
	
	
	public static OnlineOthelloOption getOption() {
		return option;
	}
	
}

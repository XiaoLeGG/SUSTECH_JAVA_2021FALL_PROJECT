package cn.sustech.othello.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cn.sustech.othello.controller.option.SimpleOthelloOption;
import cn.sustech.othello.controller.save.OthelloSave;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.othello.Othello;
import cn.sustech.othello.view.SaveCreateWindow;
import cn.sustech.othello.view.SaveSelectWindow;
import cn.sustech.othello.view.Window;

public class SaveController extends Controller {
	
	static {
		instance = new SaveController();
	}
	
	public static SaveController getController() {
		return instance;
	}
	
	private static SaveController instance;
	
	private static final String SAVE_DIRECTORY = "./Save";
	
	private HashMap<String, OthelloSave> saves;
	
	private SaveSelectWindow selectWindow;
	private SaveCreateWindow createWindow;
	
	private SaveController() {}
	
	public void initSelectWindow(SaveSelectWindow selectWindow) {
		this.selectWindow = selectWindow;
	}
	
	public void initCreateWindow(SaveCreateWindow createWindow) {
		this.createWindow = createWindow;
	}
	
	@Override
	@Deprecated
	public Window getViewer() {
		return this.selectWindow;
	}
	
	public Window getSelectViewer() {
		return this.selectWindow;
	}
	
	public Window getCreateViewer() {
		return this.createWindow;
	}
	
	public OthelloSave getSave(String name) {
		return this.saves.get(name.toLowerCase());
	}
	
	public Collection<OthelloSave> getSaves() {
		return new ArrayList<>(this.saves.values());
	}
	
	public void loadSaves() {
		saves = new HashMap<>();
		File dir = new File(SAVE_DIRECTORY);
		dir.mkdirs();
		for (File file : dir.listFiles((f) -> f.isDirectory())) {
			String name = file.getName();
			try {
				OthelloSave save = OthelloSave.loadSave(file);
				saves.put(name.toLowerCase(), save);
			} catch (Exception e) {
				ThrowableHandler.handleThrowable(e);
			}
			
		}
	}
	
	public void removeSave(String saveName) {
		this.saves.remove(saveName);
		File dir = new File(SAVE_DIRECTORY);
		dir.mkdirs();
		File saveDir = new File(dir, saveName);
		if (!saveDir.exists()) {
			return;
		}
		deleteFile(saveDir);
	}
	
	private void deleteFile(File f) {
		if (f.isDirectory()) {
			for (File subFile : f.listFiles()) {
				deleteFile(subFile);
			}
		}
		f.delete();
	}
	
	public void selectSave(String save) {
		
		WindowManager wm = WindowManager.getInstance();
		wm.getWindow(WindowManager.SAVE_SELECT_WINDOW).getStage().close();
		SimpleOthelloController con = new SimpleOthelloController();
		con.initSave(saves.get(save.toLowerCase()));
		wm.switchWindow(wm.getWindow(WindowManager.LOBBY), con.getViewer(), false);
		
		
	}
	
	public void save(OthelloSave sav) throws IOException {
		File savDir = new File(SAVE_DIRECTORY + "/" + sav.getName());
		savDir.mkdirs();
		sav.save(savDir);
	}
	
	public void createSave(String savName, SimpleOthelloOption option) {	
		File dir = new File(SAVE_DIRECTORY);
		dir.mkdirs();
		File savDir = new File(SAVE_DIRECTORY + "/" + savName);
		savDir.mkdirs();
		try {
			OthelloSave save = new OthelloSave(savName, option, null);
			save.save(savDir);
			saves.put(save.getName().toLowerCase(), save);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
	}
	
}

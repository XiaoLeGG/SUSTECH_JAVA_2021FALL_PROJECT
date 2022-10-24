package cn.sustech.othello.view;

import cn.sustech.othello.controller.Controller;
import javafx.stage.Stage;

public interface Window {
	
	public Stage getStage();
	public void resetStage();
	public String getName();
	public Controller getController();
	
}

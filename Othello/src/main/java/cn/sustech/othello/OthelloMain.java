package cn.sustech.othello;

import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.exception.ThrowableHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class OthelloMain extends Application{
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		new WindowManager().init();
	}
	
	public static void main(String args[]) {
		
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			ThrowableHandler.handleThrowable(throwable);
		});
		
		new OthelloTimer().start();
		CacheManager.getManager().init();
		Application.launch(OthelloMain.class);
	}

	
}

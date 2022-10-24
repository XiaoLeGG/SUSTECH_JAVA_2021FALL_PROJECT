package cn.sustech.othello.controller;

import java.io.IOException;
import java.util.HashMap;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;

import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.view.BalanceBoardWindow;
import cn.sustech.othello.view.Lobby;
import cn.sustech.othello.view.PlayerLoginWindow;
import cn.sustech.othello.view.SaveCreateWindow;
import cn.sustech.othello.view.SimpleOthelloWindow;
import cn.sustech.othello.view.SaveSelectWindow;
import cn.sustech.othello.view.SettingWindow;
import cn.sustech.othello.view.Window;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WindowManager {
	
	private static WindowManager wm;
	private HashMap<String, Window> windows;
	public static final String LOBBY = "lobby";
	public static final String SAVE_SELECT_WINDOW = "saveselect";
	public static final String SAVE_CREATE_WINDOW = "savecreate";
	public static final String PLAYER_LOGIN_WINDOW = "playerlogin";
	public static final String BALANCE_BOARD_WINDOW = "balanceboard";
	public static final String SETTING_WINDOW = "setting";
	
	public static WindowManager getInstance() {
		return wm;
	}
	
	public WindowManager() {
		if (wm != null) {
			throw new UnsupportedOperationException("There has been a instance of cn.sustech.othello.controller.WindowManager.class");
		}
		wm = this;
		this.windows = new HashMap<>();
		windows.put(LOBBY, new Lobby());
		windows.put(SAVE_SELECT_WINDOW, new SaveSelectWindow());
		windows.put(SAVE_CREATE_WINDOW, new SaveCreateWindow());
		windows.put(PLAYER_LOGIN_WINDOW, new PlayerLoginWindow(PlayerController.getController()));
		windows.put(BALANCE_BOARD_WINDOW, new BalanceBoardWindow());
		windows.put(SETTING_WINDOW, new SettingWindow());
		PlayerController.getController().loadPlayers();
	}
	
	public void setIcon(JFXDecorator dec) {
		ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/logo.png")));
		icon.setFitHeight(24);
		icon.setFitWidth(24);
		dec.setGraphic(icon);
	}
	
	public void setSettingButton(JFXDecorator dec) {
		
		Node[] nodes = new Node[] {((HBox) dec.getChildren().get(0)).getChildren().get(1),
		((HBox) dec.getChildren().get(0)).getChildren().get(2),
		((HBox) dec.getChildren().get(0)).getChildren().get(3),
		((HBox) dec.getChildren().get(0)).getChildren().get(4)};
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		
		SVGGlyph graphic = new SVGGlyph(0, 
				"SETTING", 
				"M1016.832 606.208q2.048 12.288-1.024 29.696t-10.24 35.328-17.408 32.256-22.528 20.48-21.504 6.144-20.48-4.096q-10.24-3.072-25.6-5.632t-31.232-1.024-31.744 6.656-27.136 17.408q-24.576 25.6-28.672 58.368t9.216 62.464q10.24 20.48-3.072 40.96-6.144 8.192-19.456 16.896t-29.184 15.872-33.28 11.264-30.72 4.096q-9.216 0-17.408-7.168t-11.264-15.36l-1.024 0q-11.264-31.744-38.4-54.784t-62.976-23.04q-34.816 0-62.976 23.04t-39.424 53.76q-5.12 12.288-15.36 17.92t-22.528 5.632q-14.336 0-32.256-5.12t-35.84-12.8-32.256-17.92-21.504-20.48q-5.12-7.168-5.632-16.896t7.68-27.136q11.264-23.552 8.704-53.76t-26.112-55.808q-14.336-15.36-34.816-19.968t-38.912-3.584q-21.504 1.024-44.032 8.192-14.336 4.096-28.672-2.048-11.264-4.096-20.992-18.944t-17.408-32.768-11.776-36.864-2.048-31.232q3.072-22.528 20.48-28.672 30.72-12.288 55.296-40.448t24.576-62.976q0-35.84-24.576-62.464t-55.296-38.912q-9.216-3.072-15.36-14.848t-6.144-24.064q0-13.312 4.096-29.696t10.752-31.744 15.36-28.16 18.944-18.944q8.192-5.12 15.872-4.096t16.896 4.096q30.72 12.288 64 7.68t58.88-29.184q12.288-12.288 17.92-30.208t7.168-35.328 0-31.744-2.56-20.48q-2.048-6.144-3.584-14.336t1.536-14.336q6.144-14.336 22.016-25.088t34.304-17.92 35.84-10.752 27.648-3.584q13.312 0 20.992 8.704t10.752 17.92q11.264 27.648 36.864 48.64t60.416 20.992q35.84 0 63.488-19.968t38.912-50.688q4.096-8.192 12.8-16.896t17.92-8.704q14.336 0 31.232 4.096t33.28 11.264 30.208 18.432 22.016 24.576q5.12 8.192 3.072 17.92t-4.096 13.824q-13.312 29.696-8.192 62.464t29.696 57.344 60.416 27.136 66.56-11.776q8.192-5.12 19.968-4.096t19.968 9.216q15.36 14.336 27.136 43.52t15.872 58.88q2.048 17.408-5.632 27.136t-15.872 12.8q-31.744 11.264-54.272 39.424t-22.528 64q0 34.816 18.944 60.928t49.664 37.376q7.168 4.096 12.288 8.192 11.264 9.216 15.36 23.552zM540.672 698.368q46.08 0 87.04-17.408t71.168-48.128 47.616-71.168 17.408-86.528-17.408-86.528-47.616-70.656-71.168-47.616-87.04-17.408-86.528 17.408-70.656 47.616-47.616 70.656-17.408 86.528 17.408 86.528 47.616 71.168 70.656 48.128 86.528 17.408z", 
				Color.WHITE);
		graphic.setSizeForHeight(16);
		JFXButton btnSetting = new JFXButton();
        btnSetting.getStyleClass().add("jfx-decorator-button");
        btnSetting.setCursor(Cursor.HAND);
        btnSetting.setRipplerFill(Color.WHITE);
        btnSetting.setOnAction((action) -> {
        	this.showWindow(SETTING_WINDOW, false);
        });
        btnSetting.setTranslateX(-30);
        btnSetting.setGraphic(graphic);
        ((HBox) dec.getChildren().get(0)).getChildren().add(btnSetting);
        
        for (Node n : nodes) {
        	((HBox) dec.getChildren().get(0)).getChildren().add(n);
        }
	}
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
	public void init() {
		new Thread(() -> {
            try {
                SVGGlyphLoader.loadGlyphsFont(WindowManager.class.getResourceAsStream("/fonts/icomoon.svg"),
                    "icomoon.svg");
            } catch (IOException e) {
            	ThrowableHandler.handleThrowable(e);
            }
        }).start();
		
		windows.get(LOBBY).resetStage();
		windows.get(SAVE_SELECT_WINDOW).resetStage();
		windows.get(SAVE_CREATE_WINDOW).resetStage();
		windows.get(PLAYER_LOGIN_WINDOW).resetStage();
		windows.get(BALANCE_BOARD_WINDOW).resetStage();
		windows.get(SETTING_WINDOW).resetStage();
		this.showWindow(LOBBY, false);
	}
	
	public void registerWindow(String name, Window window) {
		windows.put(name, window);
	}
	
	public void unregisterWindow(String name) {
		windows.remove(name);
	}
	
	public Window getWindow(String window) {
		return windows.get(window);
	}
	
	public void showWindow(String window, boolean reset) {
		this.showWindow(this.windows.get(window), reset);
	}
	
	public void showWindow(Window window, boolean reset) {
		if (reset) {
			window.resetStage();
		}
		window.getStage().show();
		window.getStage().toFront();
    	window.getStage().getIcons().clear();
		window.getStage().getIcons().add(new Image(getClass().getResourceAsStream("/logo(small).png")));
	}
	
	public void hideWindow(String window) {
		this.hideWindow(this.windows.get(window));
	}
	
	public void hideWindow(Window window) {
		window.getStage().hide();
	}
	
	public void switchWindow(String current, String to, boolean reset) {
		this.switchWindow(this.windows.get(current), this.windows.get(to), reset);
	}
	
	public void switchWindow(Window current, Window to, boolean reset) {
		if (reset) {
			to.resetStage();
		}
    	to.getStage().setX(current.getStage().getX());
    	to.getStage().setY(current.getStage().getY());
    	current.getStage().hide();
    	to.getStage().show();	
    	to.getStage().toFront();
    	to.getStage().getIcons().clear();
		to.getStage().getIcons().add(new Image(getClass().getResourceAsStream("/logo(small).png")));
	}
	
}

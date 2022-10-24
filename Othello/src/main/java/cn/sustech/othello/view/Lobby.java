package cn.sustech.othello.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDialog.DialogTransition;

import cn.sustech.othello.MusicPlayer;
import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.OnlineOthelloController;
import cn.sustech.othello.controller.PlayerController;
import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.controller.PlayerController.PlayerInformationReceiver;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.player.Player;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Lobby implements Window{
	
	private Stage stage;
	
	public Lobby() {
		
	}

	@Override
	public Stage getStage() {
		return this.stage;
	}

	@Override
	public void resetStage() {
		this.stage = new Stage();
		this.stage.setTitle("你科黑白棋");
		
		MusicPlayer.getMusicPlayer().playLobbyMusic();
		
		StackPane pane = new StackPane();
		pane.setPrefHeight(600);
		pane.setPrefWidth(960);
		
		
		JFXDecorator dec = new JFXDecorator(stage, pane);
		WindowManager.getInstance().setIcon(dec);
		WindowManager.getInstance().setSettingButton(dec);
		Scene scene = new Scene(dec, 960, 600);
		scene.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());
		
		Image image = new Image(getClass().getResourceAsStream("/background.png"));
		Background bg = new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false)));
		pane.setBackground(bg);
		
		JFXButton singlePlayButton = new JFXButton("单人游戏");
		JFXButton networkPlayButton = new JFXButton("局域网对战");
		JFXButton aboutUsButton = new JFXButton("排行榜");
		JFXButton ruleButton = new JFXButton("规则说明");
		
		singlePlayButton.setPrefHeight(56);
		singlePlayButton.setPrefWidth(162);
		singlePlayButton.setFont(new Font(24));
		
		networkPlayButton.setPrefHeight(56);
		networkPlayButton.setPrefWidth(162);
		networkPlayButton.setFont(new Font(24));
		
		aboutUsButton.setPrefHeight(56);
		aboutUsButton.setPrefWidth(162);
		aboutUsButton.setFont(new Font(24));
		
		ruleButton.setPrefHeight(56);
		ruleButton.setPrefWidth(162);
		ruleButton.setFont(new Font(24));
		
		VBox box = new VBox();
		box.setPrefWidth(162);
		box.setPrefHeight(56 * 3 + 18 * 2);
		box.setAlignment(Pos.TOP_CENTER);
		VBox.setMargin(singlePlayButton, new Insets(250, 0, 0, 0));
		box.setSpacing(18.0);
		box.getChildren().add(singlePlayButton);
		box.getChildren().add(networkPlayButton);
		box.getChildren().add(aboutUsButton);
		box.getChildren().add(ruleButton);
		
		pane.getChildren().add(box);
		
		singlePlayButton.setOnMouseClicked((mouseEvent) -> {
			WindowManager wm = WindowManager.getInstance();
			wm.showWindow(WindowManager.SAVE_SELECT_WINDOW, true);
		});
		
		networkPlayButton.setOnAction(e -> {
			WindowManager.getInstance().showWindow(WindowManager.PLAYER_LOGIN_WINDOW, true);
			PlayerController.getController().setReceiver(new PlayerInformationReceiver() {

				@Override
				public void receive(Player p) {
					OnlineOthelloController controller = new OnlineOthelloController(p);
					WindowManager.getInstance().switchWindow(WindowManager.getInstance().getWindow(WindowManager.LOBBY), controller.getOthelloViewer(), false);
				}

			});
		});
		
		aboutUsButton.setOnAction(e -> {
			WindowManager.getInstance().showWindow(WindowManager.BALANCE_BOARD_WINDOW, true);
		});
		
		ruleButton.setOnAction(e -> {
			JFXDialog dialog = new JFXDialog();
			dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
			dialog.setTransitionType(DialogTransition.CENTER);
			JFXDialogLayout layout = new JFXDialogLayout();
			layout.setHeading(new Label("学黑白棋"));
			layout.setBody(new Label("1.当您的棋使得一列、一排或是斜线方向上的敌方棋子被您的棋子夹住时（中间不得包含空隙），就可以将对方棋子转换为我方棋子\r\n"
					+ "2.每一步棋必须至少翻转一颗对手的棋子。\r\n"
					+ "3.无棋可下时，将直接轮到对方出棋，即对手连下。\r\n"
					+ "4.双方都没有棋子可以下或棋盘已满时棋局结束，以棋子数目来计算胜负，棋盘上棋子多的一方获胜。\r\n"
					+ "5.你学会了吗？"));
			JFXButton closeButton = new JFXButton("会了");
			closeButton.setOnAction(event -> {
				dialog.close();
			});
			layout.setActions(closeButton);
			closeButton.getStyleClass().add("dialog-accept");
			dialog.setContent(layout);
			dialog.setDialogContainer(pane);
			dialog.show();
		});
		
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public String getName() {
		return WindowManager.LOBBY;
	}

	@Override
	public Controller getController() {
		return null;
	}
	
}

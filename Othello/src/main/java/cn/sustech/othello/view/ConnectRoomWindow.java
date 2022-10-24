package cn.sustech.othello.view;

import java.util.ArrayList;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;

import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.OnlineOthelloController;
import cn.sustech.othello.controller.WindowManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConnectRoomWindow implements Window {
	private Stage stage;
	private OnlineOthelloController controller;
	
	@Override
	public Stage getStage() {
		return this.stage;
	}
	
	public ConnectRoomWindow(OnlineOthelloController controller) {
		this.controller = controller;
	}
	
	private void initConnectPane(StackPane parent) {
		parent.getChildren().clear();
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(40);
		
		Label title = new Label("加入房间");
		title.setFont(Font.font(25));
		vbox.getChildren().add(title);
		
		HBox addressBox = new HBox();
		JFXTextField address = new JFXTextField();
		address.setPromptText("服务器地址");
		address.setLabelFloat(true);
		address.setFont(Font.font(18));
		address.setValidators(new RequiredFieldValidator("地址不可为空"));
		
		addressBox.getChildren().add(address);
		addressBox.setAlignment(Pos.CENTER);
		
		
		ArrayList<ValidatorBase> list = new ArrayList<>();
		list.addAll(address.getValidators());
		for (ValidatorBase b : list) {
			FontIcon triangle = new FontIcon();
			triangle.setIconLiteral("fas-exclamation-triangle");
			b.setIcon(triangle);
		}
		
		vbox.getChildren().add(addressBox);
		
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(20);
		
		JFXButton confirm = new JFXButton("连接");
		confirm.setFont(Font.font(18));
		confirm.setPrefHeight(30);
		confirm.setPrefWidth(80);
		buttonBox.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
			if (ev.getCode() == KeyCode.ENTER) {
				confirm.fire();
				ev.consume();
			}
		});
		confirm.setOnAction(e -> {
			if (address.validate()) {
				
				if (this.controller.connect(address.getText())) {
					JFXDialog dialog = new JFXDialog();
					dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
					dialog.setTransitionType(DialogTransition.CENTER);
					JFXDialogLayout layout = new JFXDialogLayout();
					layout.setHeading(new Label("恭喜"));
					layout.setBody(new Label("连接成功"));
					JFXButton closeButton = new JFXButton("确认");
					closeButton.setOnAction(event -> {
						dialog.close();
					});
					layout.setActions(closeButton);
					closeButton.getStyleClass().add("dialog-accept");
					dialog.setContent(layout);
					dialog.setDialogContainer(parent);
					dialog.show();
					dialog.setOnDialogClosed(e1 -> {
						stage.close();
					});
				} else {
					address.setText("");
					JFXDialog dialog = new JFXDialog();
					dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
					dialog.setTransitionType(DialogTransition.CENTER);
					JFXDialogLayout layout = new JFXDialogLayout();
					layout.setHeading(new Label("出错"));
					layout.setBody(new Label("连接失败"));
					JFXButton closeButton = new JFXButton("确认");
					closeButton.setOnAction(event -> {
						dialog.close();
					});
					layout.setActions(closeButton);
					closeButton.getStyleClass().add("dialog-accept");
					dialog.setContent(layout);
					dialog.setDialogContainer(parent);
					dialog.show();
				}
			}
		});
		buttonBox.getChildren().add(confirm);
		vbox.getChildren().add(buttonBox);
		vbox.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());
		
		StackPane.setAlignment(vbox, Pos.TOP_CENTER);
		parent.getChildren().add(vbox);
	}

	@Override
	public void resetStage() {
		stage = new Stage();
		stage.setMaxHeight(400);
		stage.setMaxWidth(600);
		
		stage.setTitle("加入房间");
		stage.initModality(Modality.APPLICATION_MODAL);
		StackPane pane = new StackPane();
		pane.setMaxHeight(300);
		pane.setMaxWidth(500);
		pane.setStyle("-fx-background-color:WHITESMOKE");
		JFXDepthManager.setDepth(pane, 4);
		this.initConnectPane(pane);
		JFXDecorator dec = new JFXDecorator(stage, pane);
		WindowManager.getInstance().setIcon(dec);
		Scene scene = new Scene(dec, 600, 400);
		stage.setScene(scene);
	}
	

	@Override
	public String getName() {
		return WindowManager.PLAYER_LOGIN_WINDOW;
	}

	@Override
	public Controller getController() {
		return null;
	}
}

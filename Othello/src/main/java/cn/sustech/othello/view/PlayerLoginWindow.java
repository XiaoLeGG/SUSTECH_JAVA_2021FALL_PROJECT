package cn.sustech.othello.view;

import java.io.IOException;
import java.util.ArrayList;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;

import cn.sustech.othello.OthelloUtils;
import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.PlayerController;
import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.player.SimplePlayerSave;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlayerLoginWindow implements Window {
	
	private Stage stage;
	private PlayerController controller;
	
	@Override
	public Stage getStage() {
		return this.stage;
	}
	
	public PlayerLoginWindow(PlayerController controller) {
		this.controller = controller;
	}
	
	private void initRegisterPane(StackPane parent) {
		parent.getChildren().clear();
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(20);
		
		Label title = new Label("用户注册");
		title.setFont(Font.font(25));
		vbox.getChildren().add(title);
		
		VBox textBox = new VBox();
		textBox.setSpacing(10);
		textBox.setAlignment(Pos.TOP_RIGHT);
		
		HBox accountBox = new HBox();
		Label label = new Label("账号:  ");
		label.setFont(Font.font(18));
		JFXTextField account = new JFXTextField();
		account.setFont(Font.font(18));
		account.setValidators(new RequiredFieldValidator("该项为必填项"), new ValidatorBase("用户不合法") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				if (!text.getText().matches("[a-zA-z0-9\\u4e00-\\u9fa5_]{2,8}")) {
					hasErrors.set(true);
				} else {
					hasErrors.set(false);
				}
			}
			
		}, new ValidatorBase("用户已存在") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				hasErrors.set(PlayerController.getController().getPlayerSave(text.getText().toLowerCase()) != null);
			}
			
		});
		accountBox.getChildren().add(label);
		accountBox.getChildren().add(account);
		accountBox.setAlignment(Pos.CENTER);
		textBox.getChildren().add(accountBox);
		
		HBox passwordBox = new HBox();
		Label passwordLabel = new Label("密码:  ");
		passwordLabel.setFont(Font.font(18));
		JFXPasswordField password = new JFXPasswordField();
		password.setFont(Font.font(18));
		password.setValidators(new RequiredFieldValidator("密码不可为空"), new ValidatorBase("密码需为8位以上") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				hasErrors.set(text.getText().length() < 8);
			}
			
		});
		passwordBox.getChildren().add(passwordLabel);
		passwordBox.getChildren().add(password);
		passwordBox.setAlignment(Pos.CENTER);
		textBox.getChildren().add(passwordBox);
		
		HBox confirmPWBox = new HBox();
		Label confirmPWLabel = new Label("确认密码:  ");
		confirmPWLabel.setFont(Font.font(18));
		JFXPasswordField confirmPW = new JFXPasswordField();
		confirmPW.setFont(Font.font(18));
		confirmPW.focusedProperty().addListener((o, oldVal, newVal) -> {
			confirmPW.validate();
		});
		confirmPW.setValidators(new ValidatorBase("确认密码与上方不相同") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				if (!text.getText().equals(password.getText())) {
					hasErrors.set(true);
				} else {
					hasErrors.set(false);
				}
			}
			
		});
		confirmPWBox.getChildren().add(confirmPWLabel);
		confirmPWBox.getChildren().add(confirmPW);
		confirmPWBox.setAlignment(Pos.CENTER_RIGHT);
		confirmPWBox.setPadding(new Insets(0, 114, 0, 0));
		textBox.getChildren().add(confirmPWBox);
		textBox.setPadding(new Insets(0, 0, 10, 0));
		
		ArrayList<ValidatorBase> list = new ArrayList<>();
		list.addAll(account.getValidators());
		list.addAll(password.getValidators());
		list.addAll(confirmPW.getValidators());
		for (ValidatorBase b : list) {
			FontIcon triangle = new FontIcon();
			triangle.setIconLiteral("fas-exclamation-triangle");
			b.setIcon(triangle);
		}
		
		
		vbox.getChildren().add(textBox);
		
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(20);
		
		JFXButton cancel = new JFXButton("返回");
		cancel.setFont(Font.font(18));
		cancel.setPrefHeight(30);
		cancel.setPrefWidth(80);
		buttonBox.getChildren().add(cancel);
		cancel.setOnAction(e -> {
			this.initLoginPane(parent);
		});
		
		JFXButton register = new JFXButton("注册");
		register.setFont(Font.font(18));
		register.setPrefHeight(30);
		register.setPrefWidth(80);
		register.setOnAction(e -> {
			if (account.validate() && password.validate() && confirmPW.validate()) {
				PlayerController.getController().createPlayer(account.getText(), password.getText());
				JFXDialog dialog = new JFXDialog();
				dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
				dialog.setTransitionType(DialogTransition.CENTER);
				JFXDialogLayout layout = new JFXDialogLayout();
				layout.setHeading(new Label("恭喜"));
				layout.setBody(new Label("账号注册成功"));
				JFXButton closeButton = new JFXButton("确认");
				closeButton.setOnAction(event -> {
					dialog.close();
					initLoginPane(parent);
				});
				layout.setActions(closeButton);
				closeButton.getStyleClass().add("dialog-accept");
				dialog.setContent(layout);
				dialog.setDialogContainer(parent);
				dialog.show();
				
			}
		});
		confirmPWBox.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
			if (ev.getCode() == KeyCode.ENTER) {
				register.fire();
				ev.consume();
			}
		});
		buttonBox.getChildren().add(register);
			
		vbox.getChildren().add(buttonBox);
		vbox.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());
		
		StackPane.setAlignment(vbox, Pos.TOP_CENTER);
		parent.getChildren().add(vbox);
	}
	
	private void initLoginPane(StackPane parent) {
		parent.getChildren().clear();
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(40);
		
		Label title = new Label("账号登录");
		title.setFont(Font.font(25));
		vbox.getChildren().add(title);
		
		VBox textBox = new VBox();
		textBox.setSpacing(10);
		textBox.setAlignment(Pos.CENTER);
		
		HBox accountBox = new HBox();
		Label label = new Label("账号:  ");
		label.setFont(Font.font(18));
		JFXTextField account = new JFXTextField();
		account.setFont(Font.font(18));
		account.setValidators(new RequiredFieldValidator("该项为必填项"), new ValidatorBase("用户不存在") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				hasErrors.set(PlayerController.getController().getPlayerSave(text.getText().toLowerCase()) == null);
			}
			
		});
		accountBox.getChildren().add(label);
		accountBox.getChildren().add(account);
		accountBox.setAlignment(Pos.CENTER);
		textBox.getChildren().add(accountBox);
		
		HBox passwordBox = new HBox();
		Label passwordLabel = new Label("密码:  ");
		passwordLabel.setFont(Font.font(18));
		JFXPasswordField password = new JFXPasswordField();
		password.setFont(Font.font(18));
		password.setValidators(new RequiredFieldValidator("该项为必填项"), new ValidatorBase("密码错误") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				hasErrors.set(
						!PlayerController.getController().getPlayerSave(
								account.getText().toLowerCase()).getEncodedPassword().equals(
										OthelloUtils.encodedPassword(text.getText())));
			}
			
		});
		passwordBox.getChildren().add(passwordLabel);
		passwordBox.getChildren().add(password);
		passwordBox.setAlignment(Pos.CENTER);
		ArrayList<ValidatorBase> list = new ArrayList<>();
		list.addAll(account.getValidators());
		list.addAll(password.getValidators());
		for (ValidatorBase b : list) {
			FontIcon triangle = new FontIcon();
			triangle.setIconLiteral("fas-exclamation-triangle");
			b.setIcon(triangle);
		}
		
		textBox.getChildren().add(passwordBox);
		vbox.getChildren().add(textBox);
		
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(20);
		
		JFXButton register = new JFXButton("注册");
		register.setFont(Font.font(18));
		register.setPrefHeight(30);
		register.setPrefWidth(80);
		buttonBox.getChildren().add(register);
		register.setOnAction(e -> {
			this.initRegisterPane(parent);
		});
		
		
		JFXButton confirm = new JFXButton("登录");
		confirm.setFont(Font.font(18));
		confirm.setPrefHeight(30);
		confirm.setPrefWidth(80);
		confirm.setOnAction(e -> {
			if (account.validate() && password.validate()) {
				JFXDialog dialog = new JFXDialog();
				dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
				dialog.setTransitionType(DialogTransition.CENTER);
				JFXDialogLayout layout = new JFXDialogLayout();
				layout.setHeading(new Label("恭喜"));
				layout.setBody(new Label("登录成功"));
				JFXButton closeButton = new JFXButton("确认");
				closeButton.setOnAction(event -> {
					dialog.close();
					stage.hide();
					this.controller.receive(this.controller.getPlayerSave(account.getText().toLowerCase()).getPlayer());
				});
				layout.setActions(closeButton);
				closeButton.getStyleClass().add("dialog-accept");
				dialog.setContent(layout);
				dialog.setDialogContainer(parent);
				dialog.show();
			}
		});
		passwordBox.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
			if (ev.getCode() == KeyCode.ENTER) {
				confirm.fire();
				ev.consume();
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
		stage.setTitle("本地登录");
		stage.initModality(Modality.APPLICATION_MODAL);
		
		StackPane pane = new StackPane();
		pane.setMaxHeight(300);
		pane.setMaxWidth(500);
		pane.setStyle("-fx-background-color:WHITESMOKE");
		JFXDepthManager.setDepth(pane, 4);
		this.initLoginPane(pane);
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
	public PlayerController getController() {
		return this.controller;
	}
	
}

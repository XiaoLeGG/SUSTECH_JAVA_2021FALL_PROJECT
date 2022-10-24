package cn.sustech.othello.view;

import java.util.ArrayList;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;

import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.SaveController;
import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.controller.option.SimpleOthelloOption;
import cn.sustech.othello.exception.ThrowableHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaveCreateWindow implements Window {
	
	private Stage stage;
	private SaveController controller;
	
	public SaveCreateWindow() {
		controller = SaveController.getController();
		controller.initCreateWindow(this);
	}
	
	@Override
	public Stage getStage() {
		return stage;
	}

	@Override
	public void resetStage() {
		stage = new Stage();
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("新建存档");

		StackPane pane = new StackPane();
		pane.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());
		pane.setPrefHeight(800);
		pane.setPrefWidth(600);
		ScrollPane spane = new ScrollPane();
		pane.getChildren().add(spane);
		VBox contentContainer = new VBox();
		contentContainer.setPadding(new Insets(30, 30, 30, 30));
		spane.setContent(contentContainer);
		spane.setFitToWidth(true);
		spane.setMaxHeight(650);
		spane.setMaxWidth(450);;
		StackPane.setAlignment(spane, Pos.CENTER);
		
		JFXDepthManager.setDepth(spane, 4);
		
		JFXButton confirmBtn = new JFXButton("");
        confirmBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        FontIcon fi = new FontIcon();
        fi.setIconLiteral("fas-check");
        fi.setIconColor(Color.WHITE);
        fi.setIconSize(24);
        confirmBtn.setGraphic(fi);
        confirmBtn.setId("fas-check");
        confirmBtn.getStyleClass().add("main-button");
        confirmBtn.setStyle("-jfx-button-type: RAISED;"
        		+ "-fx-background-color: #66BB6A;");
        pane.getChildren().add(confirmBtn);
        StackPane.setAlignment(confirmBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(confirmBtn, new Insets(0, 30, 20, 0));
        
        JFXButton cancelBtn = new JFXButton("");
        cancelBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        FontIcon fi1 = new FontIcon();
        fi1.setIconLiteral("fas-times");
        fi1.setIconColor(Color.WHITE);
        fi1.setIconSize(24);
        cancelBtn.setGraphic(fi1);
        cancelBtn.setId("fas-times");
        cancelBtn.getStyleClass().add("main-button");
        cancelBtn.setStyle("-jfx-button-type: RAISED;"
        		+ "-fx-background-color: #EF5350;");
        pane.getChildren().add(cancelBtn);
        StackPane.setAlignment(cancelBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(cancelBtn, new Insets(0, 100, 20, 0));
		
		JFXDecorator dec = new JFXDecorator(stage, pane);
		WindowManager.getInstance().setIcon(dec);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(2);
		Scene scene = new Scene(dec, 600, 800, Color.WHITE);
		
		JFXTextField title = new JFXTextField();
		title.setLabelFloat(true);
		title.setPromptText("请输入存档名");
		title.focusedProperty().addListener((o, oldVal, newVal) -> {
			if (!newVal) {
				title.validate();
			}
		});
		title.setValidators(new RequiredFieldValidator("该项为必填项"), new ValidatorBase("存档名不合法") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				hasErrors.set(!text.getText().matches("(?!((^(con)$)|^(con)/..*|(^(prn)$)|^(prn)/..*|(^(aux)$)|^(aux)/..*|(^(nul)$)|^(nul)/..*|(^(com)[1-9]$)|^(com)[1-9]/..*|(^(lpt)[1-9]$)|^(lpt)[1-9]/..*)|^/s+|.*/s$)(^[^/////:/*/?/\"/</>/|]{1,255}$)"));
			}
			
		}, new ValidatorBase("存档已存在") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				String s = text.getText();
				hasErrors.set(controller.getSave(s.toLowerCase()) != null);
			}
			
		});
		contentContainer.getChildren().add(title);
		contentContainer.setSpacing(20);
		
		HBox first = new HBox();
		first.setSpacing(120);
		contentContainer.getChildren().add(first);
		
		JFXToggleButton button1 = new JFXToggleButton();
		first.getChildren().add(button1);
		ToggleGroup group1 = new ToggleGroup();
		button1.setToggleGroup(group1);
		button1.setText("开启罚时");
		button1.setSelected(true);
		
		VBox vbox1 = new VBox();
		vbox1.setPadding(new Insets(20, 0, 0, 0));
		vbox1.setSpacing(40);
		first.getChildren().add(vbox1);
		
		JFXTextField pttf = new JFXTextField();
		pttf.setLabelFloat(true);
		vbox1.getChildren().add(pttf);
		pttf.setPromptText("请输入可罚时次数");
		pttf.focusedProperty().addListener((o, oldVal, newVal) -> {
				pttf.validate();
		});
		pttf.setValidators(new RequiredFieldValidator("该项为必填项"), new ValidatorBase("请输入合法正整数") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				try {
					int number = Integer.parseInt(text.getText());
					hasErrors.set(number <= 0);
				} catch(Exception e) {
					hasErrors.set(true);
				}
			}
			
		});
		
		JFXTextField tlf = new JFXTextField();
		tlf.setLabelFloat(true);
		vbox1.getChildren().add(tlf);
		tlf.setPromptText("请输入每回合时间限制(s)");
		tlf.focusedProperty().addListener((o, oldVal, newVal) -> {
				tlf.validate();
		});
		tlf.setValidators(new RequiredFieldValidator("该项为必填项"), new ValidatorBase("请输入合法正整数") {

			@Override
			protected void eval() {
				TextInputControl text = (TextInputControl) srcControl.get();
				try {
					long number = Long.parseLong(text.getText());
					hasErrors.set(number <= 0);
				} catch(Exception e) {
					hasErrors.set(true);
				}
			}
			
		});
		
		group1.selectedToggleProperty().addListener((o, oldVal, newVal) -> {
			if (!button1.isSelected()) {
				pttf.setText(null);
				pttf.setDisable(true);
				pttf.resetValidation();
				tlf.setText(null);
				tlf.setDisable(true);
				tlf.resetValidation();
			} else {
				pttf.setDisable(false);
				tlf.setDisable(false);
			}
		});
		
		JFXToggleButton button2 = new JFXToggleButton();
		button2.setText("允许作弊");
		button2.setSelected(false);
		contentContainer.getChildren().add(button2);
		
		JFXTextField bsf = new JFXTextField();
		bsf.setLabelFloat(true);
		bsf.setPromptText("请输入棋盘大小");
		bsf.setDisable(true);
		contentContainer.getChildren().add(bsf);
		
		ArrayList<ValidatorBase> list = new ArrayList<>();
		list.addAll(pttf.getValidators());
		list.addAll(title.getValidators());
		list.addAll(tlf.getValidators());
		for (ValidatorBase b : list) {
			FontIcon triangle = new FontIcon();
			triangle.setIconLiteral("fas-exclamation-triangle");
			b.setIcon(triangle);
		}
		
		cancelBtn.setOnMouseClicked(e -> {
			WindowManager wm = WindowManager.getInstance();
			wm.switchWindow(WindowManager.SAVE_CREATE_WINDOW, WindowManager.SAVE_SELECT_WINDOW, true);
		});
		
		confirmBtn.setOnMouseClicked(e -> {
			boolean flag = title.validate();
			if (button1.isSelected()) {
				flag = pttf.validate() && flag;
				flag = tlf.validate() && flag;	
			}
			
			if (flag) {
				SimpleOthelloOption option = new SimpleOthelloOption(false, 0, 8, button2.isSelected(), 0);
				if (button1.isSelected()) {
					option.setHasTimeLimit(true);
					option.setTimeLimit(Long.parseLong(tlf.getText()));
					option.setTimeUpTiems(Integer.parseInt(pttf.getText()));
				}
				controller.createSave(title.getText(), option);
				WindowManager wm = WindowManager.getInstance();
				wm.switchWindow(WindowManager.SAVE_CREATE_WINDOW, WindowManager.SAVE_SELECT_WINDOW, true);
			}
			
		});
		
		stage.setScene(scene);
	}

	@Override
	public String getName() {
		return WindowManager.SAVE_CREATE_WINDOW;
	}

	@Override
	public Controller getController() {
		return this.controller;
	}
	
	public static class SaveOptionPane extends Pane {
		
	}

}

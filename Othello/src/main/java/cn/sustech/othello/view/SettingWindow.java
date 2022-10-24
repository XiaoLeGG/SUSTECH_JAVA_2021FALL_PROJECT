package cn.sustech.othello.view;

import java.util.ArrayList;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;

import cn.sustech.othello.MusicPlayer;
import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.PlayerController;
import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.controller.option.SimpleOthelloOption;
import cn.sustech.othello.model.player.AIPlayer;
import cn.sustech.othello.model.player.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingWindow implements Window {
	
	private Stage stage;
	
	@Override
	public Stage getStage() {
		return this.stage;
	}

	@Override
	public void resetStage() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		stage.setTitle("设置");
		
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
		
		JFXDecorator dec = new JFXDecorator(stage, pane);
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(2);
		WindowManager.getInstance().setIcon(dec);
		final Scene scene = new Scene(dec, 600, 800, Color.WHITE);
		
		VBox volumeBox = new VBox();
		Label volumeChange = new Label("音量");
		volumeChange.setFont(Font.font(18));
		JFXSlider slider = new JFXSlider();
		slider.setValue(MusicPlayer.getMusicPlayer().getVolume() * 100);
		slider.setOnMouseDragged(e -> {
			MusicPlayer.getMusicPlayer().setVolume(slider.getValue() * 0.01);
		});
		volumeBox.getChildren().addAll(volumeChange, slider);
		contentContainer.getChildren().add(volumeBox);
		slider.setMaxWidth(600 * 0.6);
		
		stage.setScene(scene);
		
		
		
		JFXDepthManager.setDepth(spane, 4);
		
        

		
		
		
		
	}

	@Override
	public String getName() {
		return WindowManager.SETTING_WINDOW;
	}

	@Override
	public Controller getController() {
		return null;
	}

}

package cn.sustech.othello.view;

import java.util.ArrayList;
import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.svg.SVGGlyph;

import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.PlayerController;
import cn.sustech.othello.controller.SaveController;
import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.controller.save.OthelloSave;
import cn.sustech.othello.model.player.AIPlayer;
import cn.sustech.othello.model.player.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BalanceBoardWindow implements Window {
	
	private Stage stage;
	
	@Override
	public Stage getStage() {
		return stage;
	}

	@Override
	public void resetStage() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		
		JFXScrollPane pane = new JFXScrollPane();
		Image image1 = new Image(getClass().getResourceAsStream("/main-header.jpg"));
		Background bg1 = new Background(new BackgroundImage(image1, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false)));
		pane.getMainHeader().setBackground(bg1);
		
		Image image2 = new Image(getClass().getResourceAsStream("/condensed-header.jpg"));
		Background bg2 = new Background(new BackgroundImage(image2, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false)));
		pane.getCondensedHeader().setBackground(bg2);
		
		JFXDecorator dec = new JFXDecorator(stage, new StackPane(pane));
		((HBox) dec.getChildren().get(0)).getChildren().remove(1);
		((HBox) dec.getChildren().get(0)).getChildren().remove(2);
		WindowManager.getInstance().setIcon(dec);
        final Scene scene = new Scene(dec, 600, 800, Color.WHITE);
        stage.setTitle("排行榜");
        scene.getStylesheets().add(getClass().getResource("/css/listview.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/scrollpane.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());
        stage.setScene(scene);
		
        JFXListView<HBox> list = new JFXListView<>();
        
        List<Player> pList = new ArrayList<>();
        pList.addAll(PlayerController.getController().getPlayers());
        pList.sort((p1, p2) -> {
        	double winRate1 = (p1.getTotalRounds() == 0 ? 0 : (double) p1.getWinRounds() / p1.getTotalRounds());
        	double winRate2 = (p2.getTotalRounds() == 0 ? 0 : (double) p2.getWinRounds() / p2.getTotalRounds());
        	if (winRate1 > winRate2) {
        		return -1;
        	}
        	if (winRate1 < winRate2) {
        		return 1;
        	}
        	if (p1.getTotalRounds() < p2.getTotalRounds()) {
        		return 1;
        	}
        	return -1;
        });
        int i = 0;
        for (Player p : pList) {
        	if (p instanceof AIPlayer) {
        		continue;
        	}
        	++i;
        	HBox hbox = new HBox();
            hbox.setPrefWidth(250);
            double winRate = (p.getTotalRounds() == 0 ? 0 : (double) p.getWinRounds() / p.getTotalRounds());
        	Label label = new Label(i + ". 玩家: " + p.getName() + "    总局数: " + p.getTotalRounds() + "    胜率: " + String.format("%.2f", winRate * 100) + "%");
        	label.setPrefWidth(250);
        	hbox.getChildren().add(label);
            list.getItems().add(hbox);
        }
        
        
        list.setMaxHeight(6000);
        list.setPrefHeight(600);
        list.getStyleClass().add("mylistview");

        StackPane container = new StackPane(list);
        container.setPadding(new Insets(24));
        pane.setContent(container);

        JFXButton button = new JFXButton("");
        SVGGlyph arrow = new SVGGlyph(0,
            "FULLSCREEN",
            "M402.746 877.254l-320-320c-24.994-24.992-24.994-65.516 0-90.51l320-320c24.994-24.992 65.516-24.992 90.51 0 24.994 24.994 "
            + "24.994 65.516 0 90.51l-210.746 210.746h613.49c35.346 0 64 28.654 64 64s-28.654 64-64 64h-613.49l210.746 210.746c12.496 "
            + "12.496 18.744 28.876 18.744 45.254s-6.248 32.758-18.744 45.254c-24.994 24.994-65.516 24.994-90.51 0z",
            Color.WHITE);
        arrow.setSize(30, 24);
        button.setGraphic(arrow);
        button.setRipplerFill(Color.WHITE);
        button.setOnMouseClicked(event -> {
        	stage.close();
        });
        button.setStyle("-jfx-button-type: FLAT;-fx-background-color: rgba(255,255,255,0)");
        pane.getTopBar().getChildren().add(button);
        
        Label title = new Label("排行榜");
        pane.getBottomBar().getChildren().add(title);
        title.setStyle("-fx-text-fill:WHITE; -fx-font-size: 40;");
        JFXScrollPane.smoothScrolling((ScrollPane) pane.getChildren().get(0));

        StackPane.setMargin(title, new Insets(0, 0, 0, 20));
        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        StackPane.setAlignment(button, Pos.CENTER_LEFT);
        StackPane.setMargin(button, new Insets(0, 0, 0, 20));
	}

	@Override
	public String getName() {
		return WindowManager.BALANCE_BOARD_WINDOW;
	}

	@Override
	public Controller getController() {
		return null;
	}

}

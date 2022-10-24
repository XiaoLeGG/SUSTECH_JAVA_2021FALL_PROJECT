package cn.sustech.othello.view;

import java.io.IOException;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;

import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.SaveController;
import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.controller.save.OthelloSave;
import cn.sustech.othello.exception.ThrowableHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class SaveSelectWindow implements Window {

	private Stage stage;
	private SaveController controller;
	
	public SaveSelectWindow() {
		controller = SaveController.getController();
		controller.initSelectWindow(this);
		
	}
	
	@Override
	public Stage getStage() {
		return this.stage;
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
        stage.setTitle("存档");
        scene.getStylesheets().add(getClass().getResource("/css/listview.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/scrollpane.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());
        stage.setScene(scene);
		
		
        JFXListView<HBox> list = new JFXListView<>();
        
        controller.loadSaves();
        for (OthelloSave save : this.controller.getSaves()) {
        	HBox hbox = new HBox();
            hbox.setPrefWidth(200);
        	Label label = new Label(save.getName());
        	label.setPrefWidth(200);
        	hbox.getChildren().add(label);
            hbox.setOnMouseClicked(event -> {
            	if (event.getClickCount() >= 2) {
            		this.controller.selectSave(label.getText());
            	}
            });
            list.getItems().add(hbox);
        }
        
        
        list.setMaxHeight(6000);
        list.setPrefHeight(600);
        list.getStyleClass().add("mylistview");

        StackPane container = new StackPane(list);
        container.setPadding(new Insets(24));
        pane.setContent(container);
        
        JFXButton newBtn = new JFXButton("");
        newBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        FontIcon fi = new FontIcon();
        fi.setIconLiteral("fas-plus");
        fi.setIconColor(Color.WHITE);
        fi.setIconSize(24);
        newBtn.setGraphic(fi);
        newBtn.setId("fas-plus");
        newBtn.getStyleClass().add("main-button");
        newBtn.setStyle("-jfx-button-type: RAISED;"
        		+ "-fx-background-color: #66BB6A;");
        pane.getChildren().add(newBtn);
        StackPane.setAlignment(newBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(newBtn, new Insets(0, 30, 20, 0));
        
        JFXButton deleteBtn = new JFXButton("");
        deleteBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        FontIcon fi1 = new FontIcon();
        fi1.setIconLiteral("fas-minus");
        fi1.setIconColor(Color.WHITE);
        fi1.setIconSize(24);
        deleteBtn.setGraphic(fi1);
        deleteBtn.getStyleClass().add("main-button");
        deleteBtn.setStyle("-jfx-button-type: RAISED;"
        		+ "-fx-background-color: #EF5350;");
        
        deleteBtn.setOnAction(e -> {
        	HBox selectBox = list.getSelectionModel().getSelectedItem();
        	if (selectBox == null) {
        		return;
        	}
        	String name = ((Label) selectBox.getChildren().get(0)).getText();
        	JFXDialog dialog = new JFXDialog();

    		dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
    		dialog.setTransitionType(DialogTransition.TOP);
    		JFXDialogLayout layout = new JFXDialogLayout();

    		JFXButton acceptButton = new JFXButton("确认");
    		acceptButton.getStyleClass().add("dialog-accept");
    		
    		JFXButton cancelButton = new JFXButton("取消");
    		cancelButton.getStyleClass().add("dialog-accept");
    		layout.setHeading(new Label("删除存档"));
    		layout.setBody(new Label("确认删除存档 " + name +" 吗？"));
    		
    		acceptButton.setOnAction(e1 -> {
    			list.getItems().remove(list.getSelectionModel().getSelectedIndex());
    			SaveController.getController().removeSave(name);
    			dialog.close();
    		});
    		cancelButton.setOnAction(e1 -> dialog.close());
    		
    		
    		dialog.setContent(layout);
    		layout.setActions(cancelButton, acceptButton);
    		dialog.setDialogContainer(container);
    		dialog.show();
        });
        
        pane.getChildren().add(deleteBtn);
        StackPane.setAlignment(deleteBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(deleteBtn, new Insets(0, 100, 20, 0));
        
        newBtn.setOnAction(e -> {
        	WindowManager wm = WindowManager.getInstance();
        	wm.switchWindow(WindowManager.SAVE_SELECT_WINDOW, WindowManager.SAVE_CREATE_WINDOW, true);
        });

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
        
        Label title = new Label("选择你的存档");
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
		return WindowManager.SAVE_SELECT_WINDOW;
	}

	@Override
	public Controller getController() {
		return this.controller;
	}

}

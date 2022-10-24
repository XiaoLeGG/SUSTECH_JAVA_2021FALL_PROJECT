package cn.sustech.othello.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;

import cn.sustech.othello.MusicPlayer;
import cn.sustech.othello.controller.Controller;
import cn.sustech.othello.controller.PlayerController;
import cn.sustech.othello.controller.PlayerController.PlayerInformationReceiver;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.controller.SimpleOthelloController;
import cn.sustech.othello.controller.WindowManager;
import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;
import cn.sustech.othello.model.player.AIPlayer;
import cn.sustech.othello.model.player.Player;
import cn.sustech.othello.model.player.SimplePlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SimpleOthelloWindow implements Window {

	private Canvas chessCanvas;

	private Stage stage;

	private GraphicsContext gc;

	private SimpleOthelloController controller;

	private AudioClip au;

	private StackPane playerPanes[] = new StackPane[2];

	private HBox hbox;

	private VBox pane2VBox;
	
	private HBox scoreBox;
	
	private JFXProgressBar bar;
	
	private Label timeUpLabel[];

	public SimpleOthelloWindow() {}

	public SimpleOthelloWindow(SimpleOthelloController controller) {
		this.controller = controller;
	}
	
	public void updateTimeUpTimes(int side, int times) {
		timeUpLabel[side].setText("剩余罚时次数: " + times);
	}
	
	public void updateProgressBar(double percentage) {
		if (bar == null) {
			return;
		}
		bar.setProgress(percentage);
		if (percentage <= 0.2) {
			bar.setStyle("-fx-progress-color: #E94F3C;");
			return;
		}
		if (percentage <= 0.5) {
			bar.setStyle("-fx-progress-color: #FDD835;");
			return;
		}
		bar.setStyle("-fx-progress-color: #0F9D58;");	
	}

	@Override
	public Stage getStage() {
		return this.stage;
	}
	
	public void setTitle(String title) {
		this.stage.setTitle(title);
	}

	@Override
	public void resetStage() {
		au = new AudioClip(this.getClass().getResource("/audio/bubble.wav").toString());
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		

		hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(0, 0, 0, 0));
		hbox.setPrefHeight(600);
		hbox.setPrefWidth(960);

		StackPane pane1 = new StackPane();
		pane1.setPrefHeight(600);
		pane1.setPrefWidth(255);
		StackPane subPane1 = new StackPane();
		subPane1.setMaxWidth(200);
		subPane1.setMaxHeight(450);
		pane1.getChildren().add(subPane1);
		subPane1.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
		StackPane.setAlignment(subPane1, Pos.CENTER);
		JFXDepthManager.setDepth(subPane1, 4);
		this.initEmptyPlayerBoard(subPane1, 0);
		hbox.getChildren().add(pane1);

		StackPane pane2 = new StackPane();
		pane2.setPrefWidth(450);
		pane2.setPrefHeight(600);
		pane2VBox = new VBox();
		pane2VBox.setAlignment(Pos.CENTER);
		StackPane pane2Sub1 = new StackPane();
		pane2Sub1.setPrefSize(450, (600 - 410) / 2);
		StackPane pane2Sub2 = new StackPane();
		pane2Sub2.setMinSize(450, 410);
		pane2Sub2.setPrefSize(450, 410);
		StackPane pane2Sub3 = new StackPane();
		pane2Sub3.setPrefSize(450, (600 - 410) / 2);
		pane2VBox.getChildren().addAll(pane2Sub1, pane2Sub2, pane2Sub3);
		pane2.getChildren().add(pane2VBox);
		hbox.getChildren().add(pane2);

		StackPane pane3 = new StackPane();
		pane3.setPrefHeight(600);
		pane3.setPrefWidth(255);
		StackPane subPane2 = new StackPane();
		subPane2.setMaxWidth(200);
		subPane2.setMaxHeight(450);
		pane3.getChildren().add(subPane2);
		subPane2.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
		StackPane.setAlignment(subPane2, Pos.CENTER);
		JFXDepthManager.setDepth(subPane2, 4);
		this.initEmptyPlayerBoard(subPane2, 1);
		hbox.getChildren().add(pane3);

		JFXDecorator dec = new JFXDecorator(stage, hbox);
		WindowManager.getInstance().setIcon(dec);
		WindowManager.getInstance().setSettingButton(dec);
		Scene scene = new Scene(dec, 960, 600);
		scene.getStylesheets().add(getClass().getResource("/css/general.css").toExternalForm());

		chessCanvas = new Canvas(410, 410);
		JFXDepthManager.setDepth(chessCanvas, 4);
		pane2Sub2.getChildren().add(chessCanvas);
		StackPane.setAlignment(chessCanvas, Pos.CENTER);

		gc = chessCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, chessCanvas.getWidth(), chessCanvas.getHeight());
		gc.setFill(Color.ORANGE);
		gc.fillRect(0, 0, 410, 410);
		gc.setLineWidth(5);
		gc.strokeRect(2.5, 2.5, 405, 405);
		gc.setLineWidth(2);
		for (int i = 0; i < this.controller.getOthello().getBoardSize(); ++i) {
			for (int j = 0; j < this.controller.getOthello().getBoardSize(); ++j) {
				gc.strokeRect(5 + i * 50, 5 + j * 50, 50, 50);
			}
		}
		chessCanvas.setOnMouseClicked((e) -> {
			int blockX = (int) ((e.getX() - 10) / 50);
			int blockY = (int) ((e.getY() - 10) / 50);
			this.controller.onClicked(new Coordinate(blockX, blockY));
		});
		chessCanvas.setOnMouseMoved((e) -> {
			int blockX = (int) ((e.getX() - 10) / 50);
			int blockY = (int) ((e.getY() - 10) / 50);
			this.controller.onHovered(new Coordinate(blockX, blockY));
		});
		stage.setScene(scene);

		StackPane exitPane = new StackPane();
		exitPane.setPrefSize(30, 24);
		exitPane.setMaxSize(30, 24);
		exitPane.setAlignment(Pos.TOP_LEFT);
		JFXButton exitButton = new JFXButton("");
		SVGGlyph arrow = new SVGGlyph(0, "FULLSCREEN",
				"M402.746 877.254l-320-320c-24.994-24.992-24.994-65.516 0-90.51l320-320c24.994-24.992 65.516-24.992 90.51 0 24.994 24.994 "
						+ "24.994 65.516 0 90.51l-210.746 210.746h613.49c35.346 0 64 28.654 64 64s-28.654 64-64 64h-613.49l210.746 210.746c12.496 "
						+ "12.496 18.744 28.876 18.744 45.254s-6.248 32.758-18.744 45.254c-24.994 24.994-65.516 24.994-90.51 0z",
				Color.BLACK);
		arrow.setSize(30, 24);
		exitButton.setGraphic(arrow);
		exitButton.setRipplerFill(Color.LIGHTSKYBLUE);
		exitButton.setOnMouseClicked(event -> {
			this.controller.stop();
			WindowManager.getInstance().switchWindow(this, WindowManager.getInstance().getWindow(WindowManager.LOBBY), true);
		});
		exitButton.setStyle("-jfx-button-type: FLAT;-fx-background-color: rgba(255,255,255,0)");
		exitPane.getChildren().add(exitButton);
		exitPane.setPadding(new Insets(15));
		pane1.getChildren().add(exitPane);
		StackPane.setAlignment(exitPane, Pos.TOP_LEFT);
		StackPane.setAlignment(exitButton, Pos.TOP_LEFT);
	}

	public void initPlayerInformation(int side, Player p) {
		this.initPlayerInformation(this.playerPanes[side], p, side);
	}

	public void initEmptyPlayerBoard(int side) {
		this.initEmptyPlayerBoard(this.playerPanes[side], side);
	}

	private void initPlayerInformation(StackPane parent, Player p, int side) {
		parent.getChildren().clear();
		parent.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
		playerPanes[side] = new StackPane();
		playerPanes[side].setPrefHeight(parent.getMaxHeight());
		playerPanes[side].setPrefWidth(parent.getMaxWidth());

		VBox box = new VBox();
		box.setSpacing(20);
		box.setAlignment(Pos.TOP_CENTER);

		ImageView image = new ImageView();
		BorderPane imageViewWrapper = new BorderPane(image);
		
		imageViewWrapper.setCursor(Cursor.HAND);
		imageViewWrapper.setMaxSize(128, 128);
		imageViewWrapper.setStyle("-fx-border-style: SOLID; -fx-border-width: 5;-fx-border-color: BLACK");
		File profile;
		if ((profile = PlayerController.getController().getPlayerProfile(p.getName())) != null) {
			try {
				image.setImage(new Image(new FileInputStream(profile)));
			} catch (Throwable e1) {
				image.setImage(new Image(getClass().getResourceAsStream("/profile.jpg")));
			}
		} else {
			if (p instanceof AIPlayer) {
				image.setImage(new Image(getClass().getResourceAsStream("/aiprofile.png")));
			} else {
				image.setImage(new Image(getClass().getResourceAsStream("/profile.jpg")));
			}
		}
		imageViewWrapper.setOnMouseClicked(e -> {
			if (p instanceof SimplePlayer) {
				FileChooser fc = new FileChooser();
				fc.setTitle("选择头像");
				fc.getExtensionFilters().addAll(new ExtensionFilter("图片类型", "*.jpg", "*.png", "*.gif"));
				File get = fc.showOpenDialog(stage);
				if (get != null) {
					PlayerController.getController().setPlayerProfile(p.getName().toLowerCase(), get);
					try {
						image.setImage(new Image(new FileInputStream(get)));
					} catch (FileNotFoundException e1) {}
				}
			}
		});
		image.setSmooth(true);
		image.setFitHeight(128);
		image.setFitWidth(128);
		box.getChildren().add(imageViewWrapper);
		StackPane.setAlignment(box, Pos.TOP_CENTER);
		box.setPadding(new Insets(30, 0, 0, 0));

		Label name = new Label(p.getName());
		Tooltip tip = new Tooltip("UUID: " + p.getUUID().toString());
		tip.setAutoFix(true);
		name.setTooltip(tip);
		name.setFont(Font.font(18));
		Label totalRounds = new Label(p.getTotalRounds() + " 场");
		totalRounds.setFont(Font.font(18));
		double winRate = (p.getTotalRounds() == 0 ? 0.00D : ((double) p.getWinRounds()) / p.getTotalRounds());
		Label wr = new Label(String.format("胜率: %.2f", winRate * 100) + "%");
		wr.setFont(Font.font(18));
		box.getChildren().addAll(name, totalRounds, wr);

		VBox buttonBox = new VBox();
		buttonBox.setSpacing(20);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));
		JFXButton readyButton = new JFXButton("准备");
		readyButton.setFont(Font.font(18));
		readyButton.setStyle("-jfx-button-type: RAISED;-fx-background-color: #43A047;-fx-text-fill: white;");
		readyButton.setPrefWidth(140.0);
		readyButton.setPrefHeight(35);
		readyButton.setOnAction(e -> {
			if (readyButton.getText().equals("准备")) {
				readyButton.setStyle("-jfx-button-type: RAISED;-fx-background-color: #F4511E;-fx-text-fill: white;");
				readyButton.setText("取消准备");
				this.controller.toggleReady(side);
			} else {
				readyButton.setStyle("-jfx-button-type: RAISED;-fx-background-color: #43A047;-fx-text-fill: white;");
				readyButton.setText("准备");
				this.controller.toggleReady(side);
			}
		});

		JFXButton logoutButton = new JFXButton("登出");
		logoutButton.setFont(Font.font(18));
		logoutButton.setPrefWidth(140.0);
		logoutButton.setPrefHeight(35);

		logoutButton.setOnAction(e -> {
			initEmptyPlayerBoard(parent, side);
			this.controller.logoutPlayer(side);
		});

		buttonBox.getChildren().addAll(readyButton, logoutButton);
		buttonBox.setAlignment(Pos.CENTER);

		box.getChildren().add(buttonBox);

		playerPanes[side].getChildren().add(box);
		parent.getChildren().add(playerPanes[side]);
	}
	
	public void updateScore(int score[]) {
		for (int i = 0; i < 2; ++i) {
			Label label = (Label) scoreBox.getChildren().get(i);
			label.setText("" + score[i]);
		}
	}
	
	
	private boolean cheatModesInit[] = new boolean[] {false, false};
	
	public void setCheatModeStatus(int side, boolean status) {
		if (cheatModes != null) {
			cheatModes[side].setSelected(status);
		} else {
			cheatModesInit[side] = status;
		}
	}
	
	private JFXToggleButton cheatModes[];

	public void startGame(ChessType useChess[]) {
		MusicPlayer.getMusicPlayer().playChessMusic();
		timeUpLabel = new Label[2];
		cheatModes = new JFXToggleButton[2];
		for (int i = 0; i < 2; ++i) {
			final int side = i;
			VBox parent = (VBox) playerPanes[i].getChildren().get(0);
			VBox box = (VBox) parent.getChildren().get(parent.getChildren().size() - 1);
			box.getChildren().clear();
			cheatModes[i] = new JFXToggleButton();
			cheatModes[i].setText("作弊模式");
			cheatModes[i].setToggleLineColor(Color.ORANGERED);
			cheatModes[i].setToggleColor(Color.RED);
			ToggleGroup group = new ToggleGroup();
			cheatModes[i].setToggleGroup(group);
			cheatModes[i].setDisable(!this.controller.getOption().isCheatMode());
			cheatModes[i].setSelected(cheatModesInit[i]);
			group.selectedToggleProperty().addListener((o, oldVal, newVal) -> {
				this.controller.toggleCheatMode(side);
			});
			box.getChildren().add(cheatModes[i]);
			box.setSpacing(10);
			timeUpLabel[side] = new Label("剩余罚时次数: 0");
			timeUpLabel[side].setFont(Font.font(18));
			box.getChildren().add(timeUpLabel[side]);
			
			BackgroundFill fill = new BackgroundFill(
					(useChess[i] == ChessType.BLACK ? Color.DARKSLATEBLUE : Color.LIGHTYELLOW), null, null);
			this.playerPanes[i].setBackground(new Background(fill));
			if (useChess[i] == ChessType.BLACK) {
				setAllWhite(parent);
			}
		}
		
		StackPane top = (StackPane) pane2VBox.getChildren().get(0);
		scoreBox = new HBox();
		scoreBox.setSpacing(310);
		scoreBox.setAlignment(Pos.BOTTOM_CENTER);
		Label scoreLabel[] = new Label[2];
		scoreLabel[0] = new Label("2");
		scoreLabel[1] = new Label("2");
		scoreLabel[0].setFont(Font.font(50));
		scoreLabel[1].setFont(Font.font(50));
		JFXDepthManager.setDepth(scoreBox, 4);
		scoreBox.getChildren().addAll(scoreLabel);
		top.getChildren().add(scoreBox);
		
		bar = new JFXProgressBar();
		bar.setStyle("-fx-progress-color: #0F9D58;");
		bar.setMaxSize(280, 5);
		bar.setProgress(1);
		JFXDepthManager.setDepth(bar, 4);
		top.getChildren().add(bar);

		StackPane bottom = (StackPane) pane2VBox.getChildren().get(2);
		HBox downBox = new HBox();
		downBox.setSpacing(20);
		JFXButton cancelButton = new JFXButton();
		FontIcon cancelIcon = new FontIcon();
		cancelIcon.setIconSize(22);
		cancelIcon.setFill(Color.WHITE);
		cancelIcon.setIconLiteral("fas-reply");
		cancelButton.setGraphic(cancelIcon);
		cancelButton.getStyleClass().add("game-cancel");
		cancelButton.setOnAction(e -> {
			this.controller.cancelChess();
		});
		downBox.getChildren().add(cancelButton);

		JFXButton restartButton = new JFXButton();
		FontIcon restartIcon = new FontIcon();
		restartIcon.setIconLiteral("fas-redo");
		restartIcon.setIconSize(22);
		restartIcon.setFill(Color.WHITE);
		restartButton.setGraphic(restartIcon);
		restartButton.getStyleClass().add("game-restart");
		restartButton.setOnAction(e -> {
			this.controller.resetGame();
			for (int i = 0; i < 2; ++i) {
				this.initPlayerInformation(i, this.controller.getPlayer(i));
			}
			((StackPane) (this.pane2VBox.getChildren().get(0))).getChildren().clear();
			((StackPane) (this.pane2VBox.getChildren().get(2))).getChildren().clear();
			MusicPlayer.getMusicPlayer().playLobbyMusic();
		});
		downBox.getChildren().add(restartButton);

		downBox.setAlignment(Pos.BOTTOM_CENTER);
		downBox.setPadding(new Insets(0, 0, 25, 0));
		bottom.getChildren().add(downBox);

	}

	private void setAllWhite(Pane pane) {
		for (Node node : pane.getChildren()) {
			if (node instanceof BorderPane) {
				((BorderPane) node).setStyle("-fx-border-style: SOLID; -fx-border-width: 5;-fx-border-color: WHITE");
			}
			if (node instanceof Labeled) {
				((Labeled) node).setTextFill(Color.WHITE);
			}
			if (node instanceof Pane) {
				setAllWhite((Pane) node);
			}
		}
	}

	private void initEmptyPlayerBoard(StackPane parent, int side) {
		parent.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
		parent.getChildren().clear();
		playerPanes[side] = new StackPane();
		playerPanes[side].setPrefHeight(parent.getMaxHeight());
		playerPanes[side].setPrefWidth(parent.getMaxWidth());
		VBox box = new VBox();
		box.setSpacing(20);
		JFXButton button = new JFXButton("登录玩家");
		button.setFont(Font.font(18));
		button.setPrefWidth(140.0);
		button.setPrefHeight(35);
		button.setStyle("-jfx-button-type:RAISED;-fx-background-color:BLACK;");
		box.getChildren().add(button);
		button.setOnAction(e -> {
			WindowManager.getInstance().showWindow(WindowManager.PLAYER_LOGIN_WINDOW, true);
			PlayerController.getController().setReceiver(new PlayerInformationReceiver() {

				@Override
				public void receive(Player p) {
					controller.loginPlayer(p, side);
					initPlayerInformation(parent, p, side);
				}

			});
		});
		JFXButton button2 = new JFXButton("加入人机");
		
		button2.setOnAction(e -> {
			Player p = PlayerController.getController().getPlayer("\\可爱的AI/");
			controller.loginPlayer(p, side);
			initPlayerInformation(parent, p, side);
		});
		
		button2.setFont(Font.font(18));
		button2.setPrefWidth(140.0);
		button2.setPrefHeight(35);
		button2.setStyle("-jfx-button-type:RAISED;-fx-background-color:BLACK;");
		box.getChildren().add(button2);
		playerPanes[side].getChildren().add(box);
		box.setAlignment(Pos.CENTER);
		StackPane.setAlignment(box, Pos.CENTER);
		parent.getChildren().add(playerPanes[side]);
		StackPane.setAlignment(playerPanes[side], Pos.CENTER);
	}

	public void clearBoard() {
		for (int i = 0; i < this.controller.getOthello().getBoardSize(); ++i) {
			for (int j = 0; j < this.controller.getOthello().getBoardSize(); ++j) {
				this.removeAll(new Coordinate(i, j));
			}
		}
	}

	public void drawHint(Coordinate co, ChessType side) {
		int x = co.getX();
		int y = co.getY();
		gc.setGlobalAlpha(1);
		gc.clearRect(5 + x * 50, 5 + y * 50, 50, 50);
		if (side == ChessType.WHITE) {
			gc.setFill(Color.LIGHTYELLOW);
		} else {
			gc.setFill(Color.DARKSLATEBLUE);
		}
		gc.fillRect(5 + x * 50, 5 + y * 50, 50, 50);
		gc.setStroke(Color.BLACK);
		gc.strokeRect(5 + x * 50, 5 + y * 50, 50, 50);
	}

	public void removeAll(Coordinate co) {
		gc.setGlobalAlpha(1);
		int x = co.getX();
		int y = co.getY();
		gc.clearRect(5 + x * 50, 5 + y * 50, 50, 50);
		gc.setFill(Color.ORANGE);
		gc.fillRect(5 + x * 50, 5 + y * 50, 50, 50);
		gc.setStroke(Color.BLACK);
		gc.strokeRect(5 + x * 50, 5 + y * 50, 50, 50);
	}

	public void drawHover(Coordinate co, ChessType type) {
		int x = co.getX();
		int y = co.getY();
		if (type == ChessType.BLACK) {
			gc.setGlobalAlpha(0.2);
		} else {
			gc.setGlobalAlpha(0.3);
		}
		gc.setFill(Color.valueOf(type.name()));
		gc.fillOval(5 + x * 50 + 5D, 5 + y * 50 + 5D, 40, 40);
	}

	public void drawChessWithSound(ChessType chess, Coordinate co) {
		drawChess(chess, co);
		au.play();
	}

	public void drawChess(ChessType chess, Coordinate co) {
		gc.setGlobalAlpha(1);
		int x = co.getX();
		int y = co.getY();
		this.removeAll(co);
		if (chess == ChessType.EMPTY) {
			return;
		}
		gc.setFill(Color.valueOf(chess.name()));
		gc.fillOval(5 + x * 50 + 5D, 5 + y * 50 + 5D, 40, 40);

	}

	@Override
	public String getName() {
		return "SimpleOthelloWindow";
	}

	@Override
	public Controller getController() {
		return this.controller;
	}

	public void endGame(ChessType winner) {
		MusicPlayer.getMusicPlayer().playLobbyMusic();
		JFXDialog dialog = new JFXDialog();

		dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
		dialog.setTransitionType(DialogTransition.CENTER);
		JFXDialogLayout layout = new JFXDialogLayout();

		JFXButton acceptButton = new JFXButton("确认");
		layout.setActions(acceptButton);
		acceptButton.getStyleClass().add("dialog-accept");
		dialog.setContent(layout);
		dialog.setDialogContainer((StackPane) this.pane2VBox.getChildren().get(1));
		if (winner == ChessType.EMPTY) {
			layout.setHeading(new Label("平局"));
			layout.setBody(new Label("平局就是双赢！但是不计入成绩！"));
		} else {
			int winSide = this.controller.getUseChess(0) == winner ? 0 : 1;
			Player winnerPlayer = this.controller.getPlayer(winSide);
			Player loserPlayer = this.controller.getPlayer(1 - winSide);
			layout.setHeading(new Label(winner == ChessType.BLACK ? "黑棋胜利" : "白棋胜利"));
			layout.setBody(new Label(winnerPlayer.getName() + " 战胜了 " + loserPlayer.getName() + " ！"));
		}
		this.controller.resetGame();
		for (int i = 0; i < 2; ++i) {
			this.initPlayerInformation(i, this.controller.getPlayer(i));
		}
		((StackPane) (this.pane2VBox.getChildren().get(0))).getChildren().clear();
		((StackPane) (this.pane2VBox.getChildren().get(2))).getChildren().clear();
		acceptButton.setOnAction(e -> dialog.close());
		dialog.show();

	}

}

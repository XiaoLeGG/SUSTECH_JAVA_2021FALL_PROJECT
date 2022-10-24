package cn.sustech.othello.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sustech.othello.CacheManager;
import cn.sustech.othello.MusicPlayer;
import cn.sustech.othello.OthelloTimer;
import cn.sustech.othello.controller.option.OnlineOthelloOption;
import cn.sustech.othello.controller.packet.FarewellPacket;
import cn.sustech.othello.controller.packet.HeartBeatPacket;
import cn.sustech.othello.controller.packet.Packet;
import cn.sustech.othello.controller.packet.PlayerInformationPacket;
import cn.sustech.othello.controller.packet.PlayerPutChessPacket;
import cn.sustech.othello.controller.packet.PlayerSendMessagePacket;
import cn.sustech.othello.controller.packet.PlayerToggleReadyPacket;
import cn.sustech.othello.controller.packet.StartGamePacket;
import cn.sustech.othello.controller.packet.TransferProfilePacket;
import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.ChessType;
import cn.sustech.othello.model.Coordinate;
import cn.sustech.othello.model.othello.OnlineOthello;
import cn.sustech.othello.model.player.Player;
import cn.sustech.othello.model.player.SimplePlayer;
import cn.sustech.othello.view.OnlineOthelloWindow;
import javafx.application.Platform;

public class OnlineOthelloController extends OthelloController {
	private OnlineOthello othello;
	private OnlineOthelloWindow window;
	private boolean isStarted;
	private Player players[];
	private ChessType playerSide[];
	private boolean ready[];
	private boolean myTurn;
	private int roomStatus = 0;
	private ClientThread clientThread;
	private ServerThread server;
	
	public void loginPlayer(Player p, int side) {
		players[side] = p;
	}
	
	public void logoutOnlinePlayer() {
		if (players[1] == null) {
			return;
		}
		ready[1] = false;
		players[1] = null;
		this.window.initPlayerInformation(0, players[0]);
		this.window.initEmptyPlayerBoard(1);
		if (roomStatus == 1) {
			this.createRoom();
		} else {
			this.roomStatus = 0;
		}
		this.resetGame();
		this.window.clearPane2();
		this.window.showDisconectDialog();
	}
	
	public void updateScore() {
		int score[] = new int[2];
		score[0] = score[1] = 0;
		for (int i = 0; i < this.othello.getBoardSize(); ++i) {
			for (int j = 0; j < this.othello.getBoardSize(); ++j) {
				ChessType chess = this.othello.getChess(new Coordinate(i, j));
				if (chess != ChessType.EMPTY) {
					if (playerSide[0] == chess) {
						++score[0];
					} else {
						++score[1];
					}
				}
			}
		}
		this.window.updateScore(score);
	}
	
	public void sendMessage(String message) {
		if (roomStatus == 1) {
			if (this.server != null && this.server.isConnected()) {
				this.server.sendPacket(new PlayerSendMessagePacket(message));
			}
		}
		
		if (roomStatus == 2) {
			if (this.clientThread != null && this.clientThread.isConnected()) {
				this.clientThread.sendPacket(new PlayerSendMessagePacket(message));
			}
		}
		
	}
	
	public void stop(boolean isLeaving) {
		if (roomStatus == 1) {
			this.server.setStop(isLeaving);
		}
		if (roomStatus == 2) {
			this.clientThread.setStop(isLeaving);
		}
		if (!isLeaving) {
			this.logoutOnlinePlayer();
		}
	}
	
	public Player getPlayer(int side) {
		return this.players[side];
	}
	
	public ChessType getUseChess(int side) {
		return this.playerSide[side];
	}
	
	private void sendPacket(Packet packet) {
		if (roomStatus == 0) {
			return;
		}
		if (roomStatus == 1) {
			this.server.sendPacket(packet);
			return;
		}
		if (roomStatus == 2) {
			this.clientThread.sendPacket(packet);
			return;
		}
	}
	
	public void toggleReady(int side) {
		ready[side] = !ready[side];
		this.sendPacket(new PlayerToggleReadyPacket());
		if (ready[0] && ready[1] && roomStatus == 1) {
			startGame();
		}
	}
	
	public void createRoom() {
		if (roomStatus == 2) {
			return;
		}
		server = new ServerThread(this);
		server.setDaemon(true);
		server.start();
		roomStatus = 1;
	}
	
	public boolean connect(String ip) {
		if (roomStatus != 0) {
			return false;
		}
		clientThread = new ClientThread(this, ip);
		clientThread.setDaemon(true);
		boolean flag = clientThread.connect();
		if (flag) {
			roomStatus = 2;
			this.window.onRoomConnected();
			clientThread.start();
			OthelloTimer.runTaskLater(() -> {
				clientThread.sendPacket(new PlayerInformationPacket((SimplePlayer) players[0]));
				File profile = PlayerController.getController().getPlayerProfile(players[0].getName().toLowerCase());
				
				if (profile != null) {
					try {
						this.clientThread.setTransferFile(profile, players[0].getUUID().toString() + profile.getName().substring(profile.getName().indexOf(".")).toLowerCase());
						this.clientThread.startTransferTask();
						
					} catch (Exception e) {
						ThrowableHandler.handleThrowable(e);
					}
				}
			}, 4);
		}
		
		return flag;
	}
	
	public void receivePacket(Packet packet) {
		if (packet == null) {
			return;
		}
		Platform.runLater(() -> {
			if (packet instanceof HeartBeatPacket) {
				if (roomStatus == 1) {
					this.server.beat();
				}
				if (roomStatus == 2) {
					this.clientThread.beat();
				}
			}
			
			if (packet instanceof PlayerInformationPacket) {
				PlayerInformationPacket pp = (PlayerInformationPacket) packet;
				this.loginPlayer(pp.getPlayer(), 1);
				this.window.initPlayerInformation(1, pp.getPlayer());
				if (roomStatus == 1) {
					OthelloTimer.runTaskLater(() -> {
						try {
							File profile2 = PlayerController.getController().getPlayerProfile(players[0].getName().toLowerCase());
							
							if (profile2 != null) {
								try {
									this.server.setTransferFile(profile2, players[0].getUUID().toString() + profile2.getName().substring(profile2.getName().indexOf(".")).toLowerCase());
									this.sendPacket(new TransferProfilePacket("profile"));
								} catch (Exception e) {
									ThrowableHandler.handleThrowable(e);
								}
							}
						} catch (Throwable e) {}
					}, 5);
					OthelloTimer.runTaskLater(() -> {
						try {
							File dir = CacheManager.getManager().getCacheDirectory();
							File profile = new File(dir, players[1].getUUID().toString() + ".png");
							if (!profile.exists()) {
								profile = new File(dir, players[1].getUUID().toString() + ".jpg");
								if (!profile.exists()) {
									profile = new File(dir, players[1].getUUID().toString() + ".gif");		
								}
							}
							if (profile.exists()) {
								window.setOnlineProfile(profile);
							}
						} catch(Throwable e) {}
					}, 20 * 3);
				}
			}
			
			if (packet instanceof PlayerToggleReadyPacket) {
				this.ready[1] = !this.ready[1];
				this.window.toogleOnlinePlayerReady();
				if (ready[0] && ready[1] && roomStatus == 1) {
					startGame();
				}
			}
			
			if (packet instanceof StartGamePacket) {
				if (roomStatus != 2) {
					return;
				}
				this.startGame(((StartGamePacket) packet).getMySide());
			}
			
			if (packet instanceof PlayerPutChessPacket) {
				this.putChess(((PlayerPutChessPacket) packet).getCoordinate());
			}
			
			if (packet instanceof FarewellPacket) {
				this.stop(false);
			}
			
			if (packet instanceof PlayerSendMessagePacket) {
				this.window.addChatMessage(this.players[1].getName(), packet.getContext());
			}
			
			if (packet instanceof TransferProfilePacket) {
				if (roomStatus == 2) {
					
					this.clientThread.startTransferTask();
					OthelloTimer.runTaskLater(() -> {
						try {
							File dir = CacheManager.getManager().getCacheDirectory();
							File profile = new File(dir, players[1].getUUID().toString() + ".png");
							if (!profile.exists()) {
								profile = new File(dir, players[1].getUUID().toString() + ".jpg");
								if (!profile.exists()) {
									profile = new File(dir, players[1].getUUID().toString() + ".gif");
									if (!profile.exists()) {
										return;
									}
								}
							}
							window.setOnlineProfile(profile);
						} catch (Throwable e) {}
					}, 60);
				}
			}
			
			
		});
	}
	
	public void transferFile(File file, String fileName) throws IOException {
		if (roomStatus == 1) {
			this.server.setTransferFile(file, fileName);
		}
		if (roomStatus == 2) {
			this.clientThread.setTransferFile(file, fileName);
			this.clientThread.startTransferTask();
		}
	}
	
	protected void onRoomCreated(String ip) {
		this.window.onRoomCreated(ip);
	}
	
	protected void onRoomBinded() {
		this.window.onRoomBinded();
	}
	
	public OnlineOthelloController(Player localPlayer) {
		othello = new OnlineOthello(this);
		players = new Player[2];
		players[0] = localPlayer;
		window = new OnlineOthelloWindow(this);
		window.resetStage();
		WindowManager wm = WindowManager.getInstance();
		wm.registerWindow("onlineothello", window);
		
		ready = new boolean[2];
		playerSide = new ChessType[2];
		isStarted = false;
	}
	
	@Override
	public boolean isStarted() {
		return this.isStarted;
	}
	
	@Override
	public OnlineOthello getOthello() {
		return this.othello;
	}
	
	public OnlineOthelloOption getOption() {
		return OnlineOthelloOption.getOption();
	}
	
	@Deprecated
	public void toggleCheatMode(int side) {
		//this.othello.toggleCheatMode(playerSide[side]);
	}
	
	public void resetGame() {
		this.window.clearBoard();
		if (isStarted) {
			MusicPlayer.getMusicPlayer().playLobbyMusic();
		}
		this.othello.resetGame();
		isStarted = false;
		ready = new boolean[2];
		playerSide = new ChessType[2];
	}
	
	public void startGame(ChessType mySide) {
		isStarted = true;
		this.playerSide[0] = mySide;
		this.playerSide[1] = ChessType.values()[3 - mySide.ordinal()];
		this.othello.resetGame();
		this.othello.setDefaultGame();
		this.window.startGame(this.playerSide.clone());
		this.othello.searchForAllowableCoordinate();
		changeSide(true);
	}
	
	public void startGame() {
		
		int random = (int) Math.round(Math.random());
		ChessType mySide = ChessType.values()[random + 1];
		this.sendPacket(new StartGamePacket(ChessType.values()[3 - mySide.ordinal()]));
		startGame(mySide);
		
	}
	
	public void forceEndGame() {
		isStarted = false;
		this.othello.endGame();
	}
	
	public void onEndGame(ChessType type) {
		isStarted = false;
		this.window.endGame(type);
	}

	@Override
	public OnlineOthelloWindow getOthelloViewer() {
		return this.window;
	}
	
	private void putChess(Coordinate co) {
		if (!this.othello.isValid(co)) {
			return;
		}
		this.sendPacket(new PlayerPutChessPacket(co));
		this.othello.putChess(co);
		lastHover = null;
		if (!isStarted) {
			return;
		}
		
	}
	
	public void onClicked(Coordinate co) {
		if (!isStarted) {
			return;
		}
		if (!myTurn) {
			return;
		}
		if (this.othello.isValid(co)) {
			putChess(co);
		}
		
	}
	
	private Coordinate lastHover;
	
	public void onHovered(Coordinate co) {
		if (!isStarted) {
			return;
		}
		if (!myTurn) {
			return;
		}
		if (lastHover != null) {
			this.window.removeAll(lastHover);
			if (hintList.contains(lastHover)) {
				this.window.drawHint(lastHover, this.othello.getTurn());
			}
		}
		if (!this.othello.isValid(co)) {
			return;
		}
		
		this.window.removeAll(co);
		this.window.drawHover(co, this.getOthello().getTurn());
		
		lastHover = co;
	}
	
	public void clearBoard() {
		this.window.clearBoard();
	}
	
	public void drawChess(Coordinate co, ChessType type, boolean playSound) {
		this.window.removeAll(co);
		if (playSound) {
			this.window.drawChessWithSound(type, co);
		} else {
			this.window.drawChess(type, co);
		}
	}
	
	public void flip(Coordinate co, ChessType type) {
		this.window.removeAll(co);
		this.window.drawChess(type, co);
	}
	
	public void changeSide(boolean hasPutChess) {
		if (!isStarted) {
			return;
		}
		if (!hasPutChess) {
			return;
		}
		if (playerSide[1] == this.othello.getTurn()) {
			myTurn = false;
		} else {
			myTurn = true;
		}
		
	}
	
	@Deprecated
	public void cancelChess() {
		
//		if ((players[0] instanceof AIPlayer) && (players[1] instanceof AIPlayer)) {
//			return;
//		}
//		
//		if (this.save.getLogger().getStepCount() == 0) {
//			return;
//		}
//		this.save.getLogger().popStep();
//		if (this.save.getLogger().getStepCount() > 0) {
//			this.othello.setBoard(this.save.getLogger().getPeek().getResultBoard());
//			this.othello.setTurn(ChessType.values()[3 - this.save.getLogger().getPeek().getTurn()]);
//		} else {
//			this.othello.setDefaultGame();
//			this.othello.setTurn(ChessType.WHITE);
//		}
//		
//		Player p = players[0];
//		if (playerSide[0] == this.othello.getTurn()) {
//			p = players[1];
//		}
//		
//		if ((p instanceof AIPlayer) && this.save.getLogger().getStepCount() > 0) {
//			this.cancelChess();
//			return;
//		}
//		
//		this.othello.changeSide(true);
//		try {
//			SaveController.getController().save(this.save);
//		} catch (IOException e) {
//			ThrowableHandler.handleThrowable(e);
//		}
//		this.othello.searchForAllowableCoordinate();
	}
	
	private List<Coordinate> hintList;
	
	public void updateAllowableCoordinate() {
		if (hintList != null) {
			hintList.forEach(co -> {
				if (this.othello.getChess(co) == ChessType.EMPTY) {
					this.window.removeAll(co);
				}
			});
		}
		hintList = new ArrayList<>();
		for (int i = 0; i < this.othello.getBoardSize(); ++i) {
			for (int j = 0; j < this.othello.getBoardSize(); ++j) {
				Coordinate co = new Coordinate(i, j);
				if (this.othello.isValid(co)) {
					hintList.add(co);
					this.window.drawHint(co, this.othello.getTurn());
				}
			}
		}
	}
	
	private int ticks = 0;

	public void tick() {
	}
}

package cn.sustech.othello.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import cn.sustech.othello.exception.ThrowableHandler;
import cn.sustech.othello.model.player.AIPlayer;
import cn.sustech.othello.model.player.Player;
import cn.sustech.othello.model.player.RandomAIPlayer;
import cn.sustech.othello.model.player.SimplePlayerSave;
import cn.sustech.othello.view.Window;

public class PlayerController extends Controller {
	
	static {
		instance = new PlayerController();
	}
	
	public static PlayerController getController() {
		return instance;
	}
	
	private static String PLAYERS_DIRECTORY = "./Player";
	private static String PLAYER_PROFILES[] = new String[] {"profile.png", "profile.jpg", "profile.gif"};
	
	private static PlayerController instance;
	
	private HashMap<String, SimplePlayerSave> accounts;
	private HashMap<String, Player> players;
	private HashMap<String, File> playerProfile;
	
	private PlayerController() {}
	
	@Override
	public Window getViewer() {
		return WindowManager.getInstance().getWindow(WindowManager.PLAYER_LOGIN_WINDOW);
	}
	
	private PlayerInformationReceiver currentReceiver;
	
	public void setReceiver(PlayerInformationReceiver receiver) {
		this.currentReceiver = receiver;
	}
	
	public void receive(Player p) {
		if (currentReceiver == null) {
			return;
		}
		this.currentReceiver.receive(p);
		this.currentReceiver = null;
	}
	
	public Collection<Player> getPlayers() {
		return new ArrayList<>(players.values());
	}
	
	public File getPlayerProfile(String name) {
		return playerProfile.get(name.toLowerCase());
	}
	
	public void setPlayerProfile(String playerName, File file) {
		if (file == null || !file.exists()) {
			throw new NullPointerException("Profile does not exists");
		}
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index == -1) {
			return;
		}
		String suffix = name.substring(index).toLowerCase();
		if (suffix.equals(".jpg") || suffix.equals(".gif") || suffix.equals(".png")) {
			File dir = new File(PLAYERS_DIRECTORY, playerName);
			dir.mkdirs();
			File profile = new File(dir, "profile" + suffix);
			if (!profile.exists()) {
				try {
					profile.createNewFile();
				} catch (IOException e) {
					ThrowableHandler.handleThrowable(e);
					return;
				}
			}
			try {
				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(profile);
				byte data[] = new byte[1024 * 8];
				int len = 0;
				while ((len = in.read(data)) != -1) {
					out.write(data, 0, len);
				}
				in.close();
				out.close();
				playerProfile.put(playerName.toLowerCase(), profile);
			} catch (FileNotFoundException e) {
				return;
			} catch (IOException e) {
				return;
			}
			
			
		}
	}
	
	public void loadPlayers() {
		accounts = new HashMap<>();
		players = new HashMap<>();
		playerProfile = new HashMap<>();
		File dir = new File(PLAYERS_DIRECTORY);
		dir.mkdirs();
		for (File f : dir.listFiles(f -> f.isDirectory())) {
			try {
				SimplePlayerSave sav = SimplePlayerSave.loadPlayer(f);
				if (sav != null) {
					accounts.put(f.getName().toLowerCase(), sav);
					players.put(f.getName().toLowerCase(), sav.getPlayer());
					File profile = null;
					for (String fileName : PLAYER_PROFILES) {
						profile = new File(f, fileName);
						if (profile.exists()) {
							break;
						}
					}
					if (profile.exists()) {
						playerProfile.put(f.getName().toLowerCase(), profile);
					}
				}
			} catch (IOException e) {
				ThrowableHandler.handleThrowable(e);
			}
		}
		AIPlayer randomAI = new RandomAIPlayer();
		players.put(randomAI.getName(), randomAI);
	}
	
	public SimplePlayerSave createPlayer(String account, String password) {
		SimplePlayerSave sav = SimplePlayerSave.generatePlayer(account.toLowerCase(), password);
		try {
			this.savePlayer(sav);
		} catch (IOException e) {
			ThrowableHandler.handleThrowable(e);
		}
		accounts.put(account.toLowerCase(), sav);
		players.put(account.toLowerCase(), sav.getPlayer());
		return sav;
	}
	
	public void savePlayer(String name) throws IOException {
		this.savePlayer(accounts.get(name.toLowerCase()));
	}
	
	public SimplePlayerSave getPlayerSave(String name) {
		return accounts.get(name.toLowerCase());
	}
	
	public void savePlayer(SimplePlayerSave sps) throws IOException {
		File dir = new File(PLAYERS_DIRECTORY);
		File pDir = new File(dir, sps.getPlayer().getName());
		pDir.mkdirs();
		sps.save(pDir);
	}
	
	public static interface PlayerInformationReceiver {
		public void receive(Player p);
	}

	public Player getPlayer(String playerName) {
		return players.get(playerName);
	}
	

}

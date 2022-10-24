package cn.sustech.othello.model.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import cn.sustech.othello.OthelloUtils;

public class SimplePlayerSave {
	
	private static String INF_FILE_NAME = "data.othp";
	
	private SimplePlayerSave() {}
	
	@JSONField(name = "EncodedPassword")
	private String encodedPassword;
	@JSONField(name = "Account")
	private String account;
	@JSONField(name = "Player")
	private SimplePlayer player;
	
	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public SimplePlayer getPlayer() {
		return player;
	}

	public void setPlayer(SimplePlayer player) {
		this.player = player;
	}
	
	public void setPassword(String password) {
		this.encodedPassword = OthelloUtils.encodedPassword(password);
	}
	
	public void save(File dir) throws IOException {
		File file = new File(dir, INF_FILE_NAME);
		byte[] bytes = JSON.toJSONString(this).getBytes();
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(bytes);
		out.close();
	}

	public static SimplePlayerSave loadPlayer(File dir) throws IOException {
		File infFile = new File(dir, INF_FILE_NAME);
		if (!infFile.exists()) {
			return null;
		}
		FileInputStream in = new FileInputStream(infFile);
		byte[] bytes = new byte[((Long) infFile.length()).intValue()];
		in.read(bytes);
		in.close();
		return (SimplePlayerSave) JSON.parseObject(new String(bytes), SimplePlayerSave.class);
	}
	
	public static SimplePlayerSave generatePlayer(String account, String password) {
		SimplePlayerSave sav = new SimplePlayerSave();
		sav.setAccount(account);
		sav.setPassword(password);
		sav.setPlayer(SimplePlayer.generateNewPlayer(account));
		return sav;
	}
	

	
}

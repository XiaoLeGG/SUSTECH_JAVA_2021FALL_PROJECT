package cn.sustech.othello;

import org.apache.commons.codec.binary.Base64;

public final class OthelloUtils {
	
	private OthelloUtils() {throw new NullPointerException("Could not find the constructor!");}
	
	public static String encodedPassword(String password) {
		return Base64.encodeBase64String(password.getBytes());
	}
	
}

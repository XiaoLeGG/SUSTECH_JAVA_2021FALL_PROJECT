package cn.sustech.othello;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheManager {
	
	private static CacheManager cm = new CacheManager();
	private static String CACHE_DIRECTORY = "./Cache";
	private File latestCache;
	
	private CacheManager() {}
	
	public void init() {
		File dir = new File(CACHE_DIRECTORY);
		dir.mkdirs();
	}
	
	public static CacheManager getManager() {
		return cm;
	}
	
	
	private String getFileNameForKey(String key) {
		int firstHalfLength = key.length() / 2;
		String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
		localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
		return localFilename;
	}
	
	public File saveCache(DataInputStream input, String preffix) {
		String timeKey = "" + System.nanoTime();
		String fileName = getFileNameForKey(timeKey);
		return this.saveCache(fileName, input);
	}
	
	public File getCacheDirectory() {
		File dir = new File(CACHE_DIRECTORY);
		dir.mkdirs();
		return dir;
	}
	
	public File saveCache(String fileName, DataInputStream input) {
		File dir = new File(CACHE_DIRECTORY);
		dir.mkdirs();
		File file = new File(dir, fileName);
		try {
			if (file.exists()) {
				if (!file.delete()) {
					return file;
				}
			}
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			byte data[] = new byte[1024 * 8];
			int len = 0;
			while ((len = input.read(data)) != -1) {
				out.write(data, 0, len);
			}
			out.close();
			latestCache = file;
			return file;
		} catch (Throwable e) {}
		return null;
	}
	
}

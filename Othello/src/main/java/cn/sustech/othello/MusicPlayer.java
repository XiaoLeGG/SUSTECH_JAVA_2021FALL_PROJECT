package cn.sustech.othello;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {
	private MediaPlayer mediaPlayer;
	
	private static MusicPlayer musicPlayer = new MusicPlayer();
	private double currentVolume = 0.5;
	
	private MusicPlayer() {}
	
	public static MusicPlayer getMusicPlayer() {
		return musicPlayer;
	}
	
	public void setVolume(double value) {
		this.currentVolume = value;
		if (mediaPlayer != null) {
			mediaPlayer.setVolume(value);			
		}
	}
	
	public double getVolume() {
		return this.currentVolume;
	}
	
	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}
	
	public void playLobbyMusic() {
		this.stop();
		int random = (int) Math.round(Math.random() * 1) + 1;
		mediaPlayer = new MediaPlayer(new Media(getClass().getResource("/audio/bgm" + random + ".mp3").toString()));
		mediaPlayer.setVolume(this.currentVolume);
		mediaPlayer.play();
		mediaPlayer.setOnEndOfMedia(() -> {
			playLobbyMusic();
		});
	}
	
	public void playChessMusic() {
		this.stop();
		int random = (int) Math.round(Math.random() * 37) + 1;
		mediaPlayer = new MediaPlayer(new Media(getClass().getResource("/audio/background/bgm (" + random + ").mp3").toString()));
		mediaPlayer.setVolume(this.currentVolume);
		mediaPlayer.play();
		mediaPlayer.setOnEndOfMedia(() -> {
			playChessMusic();
		});
	}
	
	
	
}

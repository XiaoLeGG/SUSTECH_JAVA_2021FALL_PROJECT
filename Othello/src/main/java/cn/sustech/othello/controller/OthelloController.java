package cn.sustech.othello.controller;

import cn.sustech.othello.model.othello.Othello;
import cn.sustech.othello.view.Window;

public abstract class OthelloController extends Controller {
	
	@Override
	public final Window getViewer() {
		return this.getOthelloViewer();
	}
	
	public abstract Othello getOthello();
	public abstract Window getOthelloViewer();
	public abstract boolean isStarted();
	
}

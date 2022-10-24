package cn.sustech.othello.model;

import com.alibaba.fastjson.annotation.JSONField;

public final class Coordinate {
	
	@JSONField(name = "X")
	private int x;
	@JSONField(name = "Y")
	private int y;
	
	private Coordinate() {}
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return this.getX() * 1000 + this.getY();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		
		if (!(object instanceof Coordinate)) {
			return false;
		}
		
		Coordinate co = (Coordinate) object;
		return co.getX() == this.getX() && co.getY() == this.getY();
	}

}

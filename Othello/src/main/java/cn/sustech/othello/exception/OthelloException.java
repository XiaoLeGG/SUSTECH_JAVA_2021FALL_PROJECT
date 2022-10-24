package cn.sustech.othello.exception;

public abstract class OthelloException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5190422730712824027L;

	public OthelloException(String reason) {
		super(reason);
	}
	
	public abstract int getCode();
	
	
	
}

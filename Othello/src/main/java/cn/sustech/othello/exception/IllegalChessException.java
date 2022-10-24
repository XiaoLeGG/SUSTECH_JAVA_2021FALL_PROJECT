package cn.sustech.othello.exception;

public class IllegalChessException extends OthelloException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4552088471808918244L;

	public IllegalChessException(String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return 102;
	}
	
}

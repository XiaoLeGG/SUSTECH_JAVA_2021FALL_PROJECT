package cn.sustech.othello.exception;

public class IllegalBoardSizeException extends OthelloException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1488272205053552636L;

	public IllegalBoardSizeException(String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return 101;
	}

}

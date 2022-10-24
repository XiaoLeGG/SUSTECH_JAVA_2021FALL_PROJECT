package cn.sustech.othello.exception;

public class NoPlayerException extends OthelloException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5231990759066859980L;

	public NoPlayerException(String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return 103;
	}

}

package cn.sustech.othello.exception;

public class IllegalStepException extends OthelloException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8142858082015931241L;

	public IllegalStepException(String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return 105;
	}

}

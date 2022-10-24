package cn.sustech.othello.exception;

public class SaveFileLostException extends OthelloException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7141155852191967223L;

	public SaveFileLostException(String reason) {
		super(reason);
	}

	@Override
	public int getCode() {
		return 104;
	}

}

package br.com.segmedic.clubflex.exception;

public class ClubFlexException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public ClubFlexException(String msg) {
		super(msg);
	}
	
	public ClubFlexException(String msg, Throwable cause) {
		super(msg, cause);
	}
}

package br.com.bolao.bolao10.exception;

public class Bolao10Exception extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public Bolao10Exception(String msg) {
		super(msg);
	}
	
	public Bolao10Exception(String msg, Throwable cause) {
		super(msg, cause);
	}
}

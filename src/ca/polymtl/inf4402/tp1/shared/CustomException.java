package ca.polymtl.inf4402.tp1.shared;


public class CustomException extends Exception {

	private String message;

	public CustomException(String message) {
		super(message);
		this.message = message;
	}
	
	public String getCustomMessage() {
		return message;
	}
}

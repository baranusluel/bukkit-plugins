package net.choicecraft.ChoiceWorks.exception;

public class PlayerNotFoundException extends Exception {
	private static final long serialVersionUID = 1112748199739832597L;

	public PlayerNotFoundException(String message) {
        super(message);
    }
}
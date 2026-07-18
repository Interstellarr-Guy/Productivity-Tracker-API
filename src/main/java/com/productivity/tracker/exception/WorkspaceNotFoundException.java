package com.productivity.tracker.exception;

public class WorkspaceNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8188292281538701197L;

	public WorkspaceNotFoundException(String message) {
        super(message);
    }
}
package me.ingrim4.tachyonizedcloud.webserver.handler.websocket;

public enum WebSocketErrors {

	INVALID_REQUEST(0, "invalid request"),
	UNKNOWN_ACTION(1, "action is unknown"),
	PAYLOAD_INCOMPLETE(2, "payload isn't complete"),
	ID_NOT_FOUND(3, "unable to find object by given id");

	WebSocketErrors(int errorId, String message) {
		this.errorId = errorId;
		this.error = String.format("[\"error\", { \"errorId\": %d, \"errorMessage\": \"%s\" }]", errorId, message);
	}

	private int errorId;
	private String error;

	public int getErrorId() {
		return errorId;
	}

	public String getError() {
		return error;
	}
}

package uk.co.tealspoon.savannah;

import org.json.JSONArray;
import org.json.JSONObject;

class PluginResult {
	
	private boolean status;
	private boolean keepCallback;
	private String message;

	/**
	 * Create a new PluginResult.
	 * @param success true if execution of the Command was successful, false otherwise.
	 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 */
	protected PluginResult(boolean status, boolean keepCallback) {
		this.status = status;
		this.keepCallback = keepCallback;
	}

	/**
	 * Create a new PluginResult.
	 * @param success true if execution of the Command was successful, false otherwise.
	 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 * @param message the result of the Command, to send to the WebView.
	 */
	protected PluginResult(boolean success, boolean keepCallback, String message) {
		this(success, keepCallback);
		this.message = "'" + message.replace("'", "\\'") + "'";
	}
	
	/**
	 * Create a new PluginResult.
	 * @param success true if execution of the Command was successful, false otherwise.
	 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 * @param message the result of the Command, to send to the WebView.
	 */
	protected PluginResult(boolean success, boolean keepCallback, int message) {
		this(success, keepCallback);
		this.message = Integer.toString(message);
	}
	
	/**
	 * Create a new PluginResult.
	 * @param success true if execution of the Command was successful, false otherwise.
	 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 * @param message the result of the Command, to send to the WebView.
	 */
	protected PluginResult(boolean success, boolean keepCallback, double message) {
		this(success, keepCallback);
		this.message = Double.toString(message);
	}

	/**
	 * Create a new PluginResult.
	 * @param success true if execution of the Command was successful, false otherwise.
	 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 * @param message the result of the Command, to send to the WebView.
	 */
	protected PluginResult(boolean success, boolean keepCallback, JSONObject message) {
		this(success, keepCallback);
		this.message = message.toString().replace("'", "\\'");
	}
	
	/**
	 * Create a new PluginResult.
	 * @param success true if execution of the Command was successful, false otherwise.
	 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 * @param message the result of the Command, to send to the WebView.
	 */
	protected PluginResult(boolean success, boolean keepCallback, JSONArray message) {
		this(success, keepCallback);
		this.message = message.toString().replace("'", "\\'");
	}
	
	/**
	 * Create a new PluginResult.
	 * @param success true if execution of the Command was successful, false otherwise.
	 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 * @param message the result of the Command, to send to the WebView.
	 */
	protected PluginResult(boolean success, boolean keepCallback, boolean message) {
		this(success, keepCallback);
		this.message = Boolean.toString(message);
	}
	
	/**
	 * Return the status of the result.
	 * @return true if execution of the Command was successful, false otherwise 
	 */
	protected boolean getStatus() {
		return status;
	}
	
	/**
	 * Return whether to keep the WebView's callbacks.
	 * @return true if the web callback should be preserved to allow further results to be returned, false otherwise.
	 */
	protected boolean getKeepCallback() {
		return keepCallback;
	}
	
	/**
	 * Return the result of the Command.
	 * @return the result of the Command, to send to the WebView.
	 */
	protected String getMessage() {
		return message;
	}
}

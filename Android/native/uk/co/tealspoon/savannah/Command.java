package uk.co.tealspoon.savannah;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;

/**
 * A Command is used to send the result of a Plugin execution back to the WebView that called it.
 * @author james.ward
 *
 */
public class Command {
	
	private String callbackId;
	private WebViewManager webViewManager;
	private boolean keepCallback;
	private WebView webView;
	private String webViewManagerName;
	private Activity activity;

	/**
	 * Create a new Command
	 * @param callbackId the value used to identify the callbacks for this Command in the WebView.
	 * @param webViewManager the manager for this Command.
	 * @param webView the WebView for this Command.
	 * @param activity the Activity which contains the given WebView.
	 */
	protected Command(String callbackId, WebViewManager webViewManager, WebView webView, Activity activity) {
		this.callbackId = callbackId;
		this.webViewManager = webViewManager;
		this.webView = webView;
		this.activity = activity;
		webViewManagerName = webViewManager.getName();
		keepCallback = true;
	}
	
	/**
	 * Returns whether a response can be sent back to the WebView.
	 * @return true if the WebView's callbacks have not yet been discarded, false otherwise.
	 */
	public boolean canSendResponse() {
		return keepCallback;
	}
	
	public WebView getWebView() {
		return webView;
	}
	
	public String getWebViewManagerName() {
		return webViewManagerName;
	}
	public Activity getActivity() {
		return activity;
	}
	
	/**
	 * Sends the given result to the WebViewManager to pass on to the WebView.
	 * @param result The result of the Command.
	 */
	private synchronized void sendPluginResult(boolean status, boolean keepCallback, String message) {
		if (canSendResponse()) {
			this.keepCallback = keepCallback;
			webViewManager.sendPluginResult(status, message, keepCallback, callbackId);
		}
		else {
			Log.e("Savannah", "Response not sent because callbacks have already been discarded.");
		}
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any).
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(boolean keepCallback) {
		sendPluginResult(true, keepCallback, null);
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(JSONArray message, boolean keepCallback) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(boolean message, boolean keepCallback) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(double message, boolean keepCallback) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(int message, boolean keepCallback) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(JSONObject message, boolean keepCallback) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(String message, boolean keepCallback) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any).
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(boolean keepCallback) {
		sendPluginResult(false, keepCallback, null);
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(JSONArray message, boolean keepCallback) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(boolean message, boolean keepCallback) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(double message, boolean keepCallback) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(int message, boolean keepCallback) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(JSONObject message, boolean keepCallback) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(String message, boolean keepCallback) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	private String messageAsString(boolean message) {
		return Boolean.toString(message);
	}
	
	private String messageAsString(int message) {
		return Integer.toString(message);
	}
	
	private String messageAsString(double message) {
		return Double.toString(message);
	}
	
	private String messageAsString(JSONObject message) {
		return message.toString().replace("'", "\\'");
	}
	
	private String messageAsString(JSONArray message) {
		return message.toString().replace("'", "\\'");
	}
	
	private String messageAsString(String message) {
		return "'" + message.replace("'", "\\'") + "'";
	}
}

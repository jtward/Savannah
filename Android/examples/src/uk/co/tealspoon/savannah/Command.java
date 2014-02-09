package uk.co.tealspoon.savannah;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Activity;
import android.webkit.WebView;

/**
 * A Command is used to send the result of a Plugin execution back to the WebView that called it.
 * @author james.ward
 *
 */
public class Command {
	
	private String callbackId;
	private WebViewManager webViewManager;
	private WebView webView;
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
	}
	
	/**
	 * Returns the WebView that executed this command.
	 * @return the WebView.
	 */
	public WebView getWebView() {
		return webView;
	}
	
	/**
	 * Returns the Activity that contains the WebView that executed this command.
	 * @return the Activity.
	 */
	public Activity getActivity() {
		return activity;
	}
	
	/**
	 * Returns the WebViewManager that manages the WebView that executed this command.
	 * @return the WebViewManager.
	 */
	public WebViewManager getWebViewManager() {
		return webViewManager;
	}
	
	/**
	 * Sends the given result to the WebViewManager to pass on to the WebView.
	 * @param result The result of the Command.
	 */
	private void sendPluginResult(PluginResult result) {
		webViewManager.sendPluginResult(result, callbackId);
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any).
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(JSONArray message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(boolean message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(double message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(int message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(JSONObject message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(String message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any).
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(JSONArray message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(boolean message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(double message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(int message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(JSONObject message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(String message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
}

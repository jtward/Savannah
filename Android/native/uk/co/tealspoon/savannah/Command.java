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
	 * @param webView The WebView for this Command.
	 * @param activity The Activity which contains the given WebView.
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
	 * @param arrayMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(JSONArray arrayMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, arrayMessage));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a boolean.
	 * @param booleanMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(boolean booleanMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, booleanMessage));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a double.
	 * @param doubleMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(double doubleMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, doubleMessage));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back an int.
	 * @param intMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(int intMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, intMessage));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a JSON object.
	 * @param objectMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(JSONObject objectMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, objectMessage));
	}
	
	/**
	 * Calls the success callback in the WebView for this command (if any), passing back a string.
	 * @param stringMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void success(String stringMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, stringMessage));
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
	 * @param arrayMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(JSONArray arrayMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, arrayMessage));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a boolean.
	 * @param booleanMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(boolean booleanMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, booleanMessage));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a double.
	 * @param doubleMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(double doubleMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, doubleMessage));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back an int.
	 * @param intMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(int intMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, intMessage));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a JSON object.
	 * @param objectMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(JSONObject objectMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, objectMessage));
	}
	
	/**
	 * Calls the error callback in the WebView for this command (if any), passing back a string.
	 * @param stringMessage the result of the Command, to send to the WebView.
	 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
	 */
	public void error(String stringMessage, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, stringMessage));
	}
}

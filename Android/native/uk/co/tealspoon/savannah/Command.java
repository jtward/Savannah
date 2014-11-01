package uk.co.tealspoon.savannah;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

/**
 * A Command is used to send the result of a Plugin execution back to the WebView that called it.
 * @author james.ward
 *
 */
public class Command {

    private JSONArray arguments;
	private String callbackId;
	private WebViewManager webViewManager;
	private boolean isDiscarded;
	private String webViewManagerName;
	private Activity activity;

	/**
	 * Create a new Command
     * @param arguments the arguments passed to the plugin by the WebView.
	 * @param callbackId the value used to identify the callbacks for this Command in the WebView.
	 * @param webViewManager the manager for this Command.
	 * @param activity the Activity which contains the given WebView.
	 */
	protected Command(JSONArray arguments, String callbackId, WebViewManager webViewManager, Activity activity) {
		this.arguments = arguments;
        this.callbackId = callbackId;
		this.webViewManager = webViewManager;
		this.activity = activity;
		webViewManagerName = webViewManager.getName();
		isDiscarded = false;
	}

    public JSONArray getArguments() {
        return arguments;
    }

	public String getWebViewManagerName() {
		return webViewManagerName;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	/**
	 * Sends the given result to the WebViewManager to pass on to the WebView.
     * @param success The result of the Command.
     */
	private synchronized void sendPluginResult(boolean success, String message, boolean keepCallback) {
		if (!isDiscarded) {
			
			if (!keepCallback) {
				this.isDiscarded = true;
			}
			
			webViewManager.sendPluginResult(success, message, keepCallback, callbackId);
		}
		else {
			Log.e("Savannah", "Response not sent because callbacks have already been discarded.");
		}
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any).
	 */
	public void success() {
		sendPluginResult(true, null, false);
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(JSONArray message) {
		sendPluginResult(true, messageAsString(message), false);
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(boolean message) {
		sendPluginResult(true, messageAsString(message), false);
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(double message) {
		sendPluginResult(true, messageAsString(message), false);
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(int message) {
		sendPluginResult(true, messageAsString(message), false);
	}

	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(JSONObject message) {
		sendPluginResult(true, messageAsString(message), false);
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(String message) {
		sendPluginResult(true, messageAsString(message), false);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any).
	 */
	public void error() {
		sendPluginResult(false, null, false);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(JSONArray message) {
		sendPluginResult(false, messageAsString(message), false);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(boolean message) {
		sendPluginResult(false, messageAsString(message), false);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(double message) {
		sendPluginResult(false, messageAsString(message), false);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(int message) {
		sendPluginResult(false, messageAsString(message), false);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(JSONObject message) {
		sendPluginResult(false, messageAsString(message), false);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(String message) {
		sendPluginResult(false, messageAsString(message), false);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any).
	 */
	public void progress() {
		sendPluginResult(false, null, true);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(JSONArray message) {
		sendPluginResult(false, messageAsString(message), true);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(boolean message) {
		sendPluginResult(false, messageAsString(message), true);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(double message) {
		sendPluginResult(false, messageAsString(message), true);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(int message) {
		sendPluginResult(false, messageAsString(message), true);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(JSONObject message) {
		sendPluginResult(false, messageAsString(message), true);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(String message) {
		sendPluginResult(false, messageAsString(message), true);
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

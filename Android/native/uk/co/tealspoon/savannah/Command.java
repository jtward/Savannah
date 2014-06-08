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
	private boolean isDiscarded;
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
		keepCallback = false;
		isDiscarded = false;
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
	 * Sets the Command's keep callback flag.
	 * @param keepCallback true if the next call to success or error should keep this Command and not discard it, false otherwise.
	 * @deprecated You should only set this property when porting over plugins that use Cordova syntax. Use progress instead.
	 */
	@Deprecated
	public void setKeepCallback(boolean keepCallback) {
		this.keepCallback = keepCallback;
	}
	
	/**
	 * Sends the given result to the WebViewManager to pass on to the WebView.
	 * @param result The result of the Command.
	 */
	private synchronized void sendPluginResult(boolean status, boolean keepCallback, String message) {
		if (!isDiscarded) {
			this.keepCallback = keepCallback;
			
			if (!this.keepCallback) {
				this.isDiscarded = true;
			}
			
			webViewManager.sendPluginResult(status, message, keepCallback, callbackId);
		}
		else {
			Log.e("Savannah", "Response not sent because callbacks have already been discarded.");
		}
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any).
	 */
	public void success() {
		sendPluginResult(true, keepCallback, null);
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(JSONArray message) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(boolean message) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(double message) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(int message) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(JSONObject message) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and handlers in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void success(String message) {
		sendPluginResult(true, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any).
	 */
	public void error() {
		sendPluginResult(false, keepCallback, null);
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(JSONArray message) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(boolean message) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(double message) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(int message) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(JSONObject message) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the error callback and handlers in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void error(String message) {
		sendPluginResult(false, keepCallback, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any).
	 */
	public void progress() {
		sendPluginResult(false, true, null);
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a JSON array.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(JSONArray message) {
		sendPluginResult(false, true, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a boolean.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(boolean message) {
		sendPluginResult(false, true, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a double.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(double message) {
		sendPluginResult(false, true, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back an int.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(int message) {
		sendPluginResult(false, true, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a JSON object.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(JSONObject message) {
		sendPluginResult(false, true, messageAsString(message));
	}
	
	/**
	 * Calls the success callback and progress handlers in the WebView for this command (if any), passing back a string.
	 * @param message the result of the Command, to send to the WebView.
	 */
	public void progress(String message) {
		sendPluginResult(false, true, messageAsString(message));
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

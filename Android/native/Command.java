package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;

public class Command {
	public JSONArray arguments;
	private int callbackId;
	public WebViewManager webViewManager;
	public WebView webView;
	public Activity activity;

	public Command(String arguments, int callbackId, WebViewManager webViewManager, WebView webView, Activity activity) {
		this.arguments = massageArguments(arguments);
		this.callbackId = callbackId;
		this.webViewManager = webViewManager;
		this.webView = webView;
		this.activity = activity;
	}
	
	private JSONArray massageArguments(String arguments) {
		try {
			JSONArray args = new JSONArray(arguments);
			return args;
		} catch (JSONException e) {
			Log.d("Savannah", "Malformed JSON: " + arguments);
			return new JSONArray();
		}
	}
	
	private void sendPluginResult(PluginResult result) {
		webViewManager.sendPluginResult(result, callbackId);
	}
	
	public void success(boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback));
	}
	
	public void success(JSONArray message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	public void success(boolean message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	public void success(double message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	public void success(int message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	public void success(JSONObject message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	public void success(String message, boolean keepCallback) {
		sendPluginResult(new PluginResult(true, keepCallback, message));
	}
	
	public void error(boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback));
	}
	
	public void error(JSONArray message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	public void error(boolean message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	public void error(double message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	public void error(int message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	public void error(JSONObject message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
	
	public void error(String message, boolean keepCallback) {
		sendPluginResult(new PluginResult(false, keepCallback, message));
	}
}

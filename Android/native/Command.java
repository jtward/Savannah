package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.webkit.WebView;

public class Command {
	
	private int callbackId;
	private WebViewManager webViewManager;
	private WebView webView;
	private Activity activity;

	public Command(int callbackId, WebViewManager webViewManager, WebView webView, Activity activity) {
		this.callbackId = callbackId;
		this.webViewManager = webViewManager;
		this.webView = webView;
		this.activity = activity;
	}
	
	public WebView getWebView() {
		return webView;
	}
	
	public Activity getActivity() {
		return activity;
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

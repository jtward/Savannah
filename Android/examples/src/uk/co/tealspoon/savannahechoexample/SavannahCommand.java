package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.webkit.WebView;

public class SavannahCommand {
	public JSONArray arguments;
	private int callbackId;
	public SavannahWebViewManager webViewManager;
	public WebView webView;

	public SavannahCommand(String arguments, int callbackId, SavannahWebViewManager webViewManager, WebView webView) {
		this.arguments = massageArguments(arguments);
		this.callbackId = callbackId;
		this.webViewManager = webViewManager;
		this.webView = webView;
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
	
	private void sendPluginResult(SavannahPluginResult result) {
		webViewManager.sendPluginResult(result, webView, callbackId);
	}
	
	public void success(boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, keepCallback));
	}
	
	public void success(JSONArray message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, keepCallback, message));
	}
	
	public void success(boolean message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, keepCallback, message));
	}
	
	public void success(double message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, keepCallback, message));
	}
	
	public void success(int message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, keepCallback, message));
	}
	
	public void success(JSONObject message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, keepCallback, message));
	}
	
	public void success(String message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, keepCallback, message));
	}
	
	public void error(boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(false, keepCallback));
	}
	
	public void error(JSONArray message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(false, keepCallback, message));
	}
	
	public void error(boolean message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(false, keepCallback, message));
	}
	
	public void error(double message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(false, keepCallback, message));
	}
	
	public void error(int message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(false, keepCallback, message));
	}
	
	public void error(JSONObject message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(false, keepCallback, message));
	}
	
	public void error(String message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(false, keepCallback, message));
	}
}

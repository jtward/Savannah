package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.webkit.WebView;

public class SavannahCommand {
	public JSONArray arguments;
	private int callbackId;
	private String className;
	private String methodName;
	private SavannahWebViewManager webViewManager;
	private WebView webView;

	public SavannahCommand(String arguments, int callbackId, String className, String methodName, SavannahWebViewManager webViewManager, WebView webView) {
		this.arguments = massageArguments(arguments);
		this.callbackId = callbackId;
		this.className = className;
		this.methodName = methodName;
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
		sendPluginResult(new SavannahPluginResult(true));
	}
	
	public void success(JSONArray message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, message));
	}
	
	public void success(boolean message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, message));
	}
	
	public void success(double message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, message));
	}
	
	public void success(int message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, message));
	}
	
	public void success(JSONObject message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, message));
	}
	
	public void success(String message, boolean keepCallback) {
		sendPluginResult(new SavannahPluginResult(true, message));
	}
}

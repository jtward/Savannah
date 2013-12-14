package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;
import org.json.JSONObject;

public class SavannahPluginResult {
	public boolean status;
	public boolean keepCallback;
	public String message;

	public SavannahPluginResult(boolean success, boolean keep) {
		status = success;
		keepCallback = keep;
	}

	public SavannahPluginResult(boolean success, boolean keepCallback, String message) {
		this(success, keepCallback);
		this.message = "\"" + message + "\"";
	}
	
	public SavannahPluginResult(boolean success, boolean keepCallback, int message) {
		this(success, keepCallback);
		this.message = Integer.toString(message);
	}
	
	public SavannahPluginResult(boolean success, boolean keepCallback, double message) {
		this(success, keepCallback);
		this.message = Double.toString(message);
	}

	public SavannahPluginResult(boolean success, boolean keepCallback, JSONObject message) {
		this(success, keepCallback);
		this.message = "\"" + message.toString() + "\"";
	}
	
	public SavannahPluginResult(boolean success, boolean keepCallback, JSONArray message) {
		this(success, keepCallback);
		this.message = "\"" + message.toString() + "\"";
	}
	
	public SavannahPluginResult(boolean success, boolean keepCallback, boolean message) {
		this(success, keepCallback);
		this.message = Boolean.toString(message);
	}
	
	public String argumentsAsJSON() {
		return message;
	}

}

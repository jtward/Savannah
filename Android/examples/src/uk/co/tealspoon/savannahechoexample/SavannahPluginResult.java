package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;
import org.json.JSONObject;

public class SavannahPluginResult {
	public boolean status;
	public boolean keepCallback;
	public String message;

	public SavannahPluginResult(boolean success) {
		status = success;
		keepCallback = false;
	}

	public SavannahPluginResult(boolean b, String message) {
		this(b);
		this.message = "\"" + message + "\"";
	}
	
	public SavannahPluginResult(boolean b, int message) {
		this(b);
		this.message = Integer.toString(message);
	}
	
	public SavannahPluginResult(boolean b, double message) {
		this(b);
		this.message = Double.toString(message);
	}

	public SavannahPluginResult(boolean b, JSONObject message) {
		this(b);
		this.message = "\"" + message.toString() + "\"";
	}
	
	public SavannahPluginResult(boolean b, JSONArray message) {
		this(b);
		this.message = "\"" + message.toString() + "\"";
	}
	
	public SavannahPluginResult(boolean b, boolean message) {
		this(b);
		this.message = Boolean.toString(message);
	}
	
	public String argumentsAsJSON() {
		return message;
	}

}

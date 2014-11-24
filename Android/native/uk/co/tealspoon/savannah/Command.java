package uk.co.tealspoon.savannah;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

/**
 * A Command is used to send the result of a Plugin execution back to the WebView that called it.
 * @author james.ward
 *
 */
public class Command {

	private final JSONArray arguments;
	private final String callbackId;
	private final WebViewManager webViewManager;
	private boolean isDiscarded;
	public final String webViewManagerName;
	public final Activity activity;

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

	/**
	 * Return the length of the arguments array for this Command.
	 * @return the length of the arguments array for this Command.
	 */
	public int argumentsLength() {
		return arguments.length();
	}

	private boolean hasTypeAtIndex(int index, Class aClass) {
		if (arguments.length() > index) {
			try {
				Object argument = arguments.get(index);
				return aClass.isInstance(argument);
			}
			catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Return true if the argument at the given index is a JSON array. If index is beyond the end of the array, then this method returns false.
	 * @param index the index into the arguments array to check.
	 * @return true if the argument at the given index is a JSON array, false otherwise.
	 */
	public boolean hasArrayAtIndex(int index) {
		return arguments.optJSONArray(index) != null;
	}

	/**
	 * Returns the argument at the given index if it is a JSON array. If the argument at the given index is not a JSON array, then this method returns null.
	 * @param index the index into the arguments array to check.
	 * @return the argument at the given index if it is a JSON array, null otherwise.
	 */
	public JSONArray arrayAtIndex(int index) {
		return arguments.optJSONArray(index);
	}

	/**
	 * Returns the argument at the given index if it is a JSON array. If the argument at the given index is not a JSON array, then this method returns null.
	 * @param index the index into the arguments array to check.
	 * @param defaultValue the value to return if the argument at the given index is not a JSON array.
	 * @return the argument at the given index if it is a JSON array, null otherwise.
	 */
	public JSONArray arrayAtIndex(int index, JSONArray defaultValue) {
		if (hasArrayAtIndex(index)) {
			return arguments.optJSONArray(index);
		}
		else {
			return defaultValue;
		}
	}

	/**
	 * Return true if the argument at the given index is a boolean. If index is beyond the end of the array, then this method returns false.
	 * @param index the index into the arguments array to check.
	 * @return true if the argument at the given index is a bool, false otherwise.
	 */
	public boolean hasBooleanAtIndex(int index) {
		return hasTypeAtIndex(index, Boolean.class);
	}

	/**
	 * Returns the argument at the given index if it is a boolean. If the argument at the given index is not a boolean, then this method returns false.
	 * @param index the index into the arguments array to check.
	 * @return the argument at the given index if it is a boolean, false otherwise.
	 */
	public boolean booleanAtIndex(int index) {
		return arguments.optBoolean(index, false);
	}

	/**
	 * Returns the argument at the given index if it is a boolean. If the argument at the given index is not a boolean, then this method returns defaultValue.
	 * @param index the index into the arguments array to check.
	 * @param defaultValue the value to return if the argument at the given index is not a boolean.
	 * @return the argument at the given index if it is a boolean, defaultValue otherwise.
	 */
	public boolean booleanAtIndex(int index, boolean defaultValue) {
		return arguments.optBoolean(index, defaultValue);
	}

	/**
	 * Return true if the argument at the given index is a double. If index is beyond the end of the array, then this method returns false.
	 * @param index the index into the arguments array to check.
	 * @return true if the argument at the given index is a double, false otherwise.
	 */
	public boolean hasDoubleAtIndex(int index) {
		// use Number here because integers should also be considered doubles
		return hasTypeAtIndex(index, Number.class);
	}

	/**
	 * Returns the argument at the given index if it is a double. If the argument at the given index is not a double, then this method returns 0.
	 * @param index the index into the arguments array to check.
	 * @return the argument at the given index if it is a double, 0 otherwise.
	 */
	public double doubleAtIndex(int index) {
		// doubleAtIndex returns NaN as a fallback by default, so pass 0 instead
		return arguments.optDouble(index, 0);
	}

	/**
	 * Returns the argument at the given index if it is a double. If the argument at the given index is not a double, then this method returns defaultValue.
	 * @param index the index into the arguments array to check.
	 * @param defaultValue the value to return if the argument at the given index is not a double.
	 * @return the argument at the given index if it is a double, defaultValue otherwise.
	 */
	public double doubleAtIndex(int index, double defaultValue) {
		return arguments.optDouble(index, defaultValue);
	}

	/**
	 * Return true if the argument at the given index is an int. If index is beyond the end of the array, then this method returns false.
	 * @param index the index into the arguments array to check.
	 * @return true if the argument at the given index is an int, false otherwise.
	 */
	public boolean hasIntAtIndex(int index) {
		return hasTypeAtIndex(index, Integer.class);
	}

	/**
	 * Returns the argument at the given index if it is an int. If the argument at the given index is not an int, then this method returns 0.
	 * @param index the index into the arguments array to check.
	 * @return the argument at the given index if it is an int, 0 otherwise.
	 */
	public int intAtIndex(int index) {
		return arguments.optInt(index);
	}

	/**
	 * Returns the argument at the given index if it is an int. If the argument at the given index is not an int, then this method returns defaultValue.
	 * @param index the index into the arguments array to check.
	 * @param defaultValue the value to return if the argument at the given index is not an int.
	 * @return the argument at the given index if it is an int, 0 otherwise.
	 */
	public int intAtIndex(int index, int defaultValue) {
		return arguments.optInt(index, defaultValue);
	}

	/**
	 * Return true if the argument at the given index is a JSON object. If index is beyond the end of the array, then this method returns false.
	 * @param index the index into the arguments array to check.
	 * @return true if the argument at the given index if it is a JSON object, false otherwise.
	 */
	public boolean hasObjectAtIndex(int index) {
		return arguments.optJSONObject(index) != null;
	}

	/**
	 * Returns the argument at the given index if it is a JSON object. If the argument at the given index is not a JSON object, then this method returns null.
	 * @param index the index into the arguments array to check.
	 * @return the argument at the given index if it is a JSON object, null otherwise.
	 */
	public JSONObject objectAtIndex(int index) {
		return arguments.optJSONObject(index);
	}

	/**
	 * Returns the argument at the given index if it is a JSON object. If the argument at the given index is not a JSON object, then this method returns null.
	 * @param index the index into the arguments array to check.
	 * @param defaultValue the value to return if the argument at the given index is not a JSON object.
	 * @return the argument at the given index if it is a JSON object, null otherwise.
	 */
	public JSONObject objectAtIndex(int index, JSONObject defaultValue) {
		if (hasObjectAtIndex(index)) {
			return arguments.optJSONObject(index);
		}
		else {
			return defaultValue;
		}
	}

	/**
	 * Return true if the argument at the given index is a string. If index is beyond the end of the array, then this method returns false.
	 * @param index the index into the arguments array to check.
	 * @return true if the argument at the given index if it is a string, false otherwise.
	 */
	public boolean hasStringAtIndex(int index) {
		return hasTypeAtIndex(index, String.class);
	}

	/**
	 * Returns the argument at the given index if it is a string. If the argument at the given index is not a string, then this method returns null.
	 * @param index the index into the arguments array to check.
	 * @return the argument at the given index if it is a string, null otherwise.
	 */
	public String stringAtIndex(int index) {
		return arguments.optString(index);
	}

	/**
	 * Returns the argument at the given index if it is a string. If the argument at the given index is not a string, then this method returns null.
	 * @param index the index into the arguments array to check.
	 * @param defaultValue the value to return if the argument at the given index is not a string.
	 * @return the argument at the given index if it is a string, null otherwise.
	 */
	public String stringAtIndex(int index, String defaultValue) {
		if (hasStringAtIndex(index)) {
			return arguments.optString(index);
		}
		else {
			return defaultValue;
		}
	}

	/**
	 * Return true if the argument at the given index is null. If index is beyond the end of the array, then this method returns true.
	 * @param index the index into the arguments array to check.
	 * @return true if the argument at the given index if it is null, false otherwise.
	 */
	public boolean hasNullAtIndex(int index) {
		try {
			return (index >= 0 &&
					arguments.length() > index &&
					arguments.get(index) == null);
		}
		catch (JSONException e) {
			return true;
		}
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

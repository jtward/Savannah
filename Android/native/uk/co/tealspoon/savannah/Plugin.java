package uk.co.tealspoon.savannah;

import java.util.Collection;

import org.json.JSONArray;

/**
 * A Plugin is a class whose {@link #execute execute} method can be invoked by a {@link android.webkit.WebView WebView}. 
 * @author james.ward
 *
 */
public interface Plugin {
	
	/**
	 * Returns the name of the Plugin. This name is used in the WebView to identify the Plugin that should receive the command and should be a reversed FQDN.
	 * @return the name of the Plugin.
	 */
	public String getName();
	
	/**
	 * Returns the available methods of the Plugin.
	 * @return the available methods of the Plugin.
	 */
	public Collection<String> getMethods();
	
	/**
	 * Called when a the WebView executes a command for this Plugin.
	 * @param action The name of the action to perform.
	 * @param command The command used to feed back the result or status of the command.
	 * @return true if the given action is valid, false otherwise.
	 */
	public boolean execute(String action, Command command);
}

package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;

import android.app.Activity;
import android.webkit.WebView;

import uk.co.tealspoon.savannah.Command;
import uk.co.tealspoon.savannah.Plugin;
import uk.co.tealspoon.savannah.WebViewManager;

public class EchoPlugin implements Plugin {

	@Override
	public String getName() {
		return "uk.co.tealspoon.savannah.echo";
	}

	@Override
	public boolean execute(String action, JSONArray args, Command command) {
		
		if(action.equals("echo")) {
			String message = args.optString(0);
			command.success(message);
			return true;
		}
		
		return false;
	}
}

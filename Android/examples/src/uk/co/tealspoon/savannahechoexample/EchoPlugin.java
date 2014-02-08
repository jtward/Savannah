package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;

public class EchoPlugin implements Plugin {

	@Override
	public String getName() {
		return "uk.co.tealspoon.savannah.echo";
	}

	@Override
	public boolean execute(String action, JSONArray args,
			Command command) {
		
		if(action.equals("echo")) {
			String message = args.optString(0);
			command.success(message, false);
			return true;
		}
		
		return false;
	}
}

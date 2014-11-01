package uk.co.tealspoon.savannahechoexample;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;

import uk.co.tealspoon.savannah.Command;
import uk.co.tealspoon.savannah.Plugin;

public class EchoPlugin implements Plugin {
	
	private static final String name = "uk.co.tealspoon.savannah.echo";
	private static final List<String> methods = Arrays.asList("echo");

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Collection<String> getMethods() {
		return methods;
	}

	@Override
	public boolean execute(String action, Command command) {
		
		if(action.equals("echo")) {
			String message = command.getArguments().optString(0);
			command.success(message);
			return true;
		}
		
		return false;
	}
}

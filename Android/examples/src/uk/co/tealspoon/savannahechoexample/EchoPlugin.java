package uk.co.tealspoon.savannahechoexample;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

			if (command.hasStringAtIndex(0)) {
				String message = command.stringAtIndex(0);
				command.success(message);
			}
			else {
				command.error();
			}
			return true;
		}
		
		return false;
	}
}

package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;

public interface SavannahPlugin {
	public String getName();
	public boolean execute(String action, JSONArray args, SavannahCommand command);
}

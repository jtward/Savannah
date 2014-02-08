package uk.co.tealspoon.savannahechoexample;

import org.json.JSONArray;

public interface Plugin {
    public String getName();
    public boolean execute(String action, JSONArray args, Command command);
}

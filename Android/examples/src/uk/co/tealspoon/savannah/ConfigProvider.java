package uk.co.tealspoon.savannah;

import org.json.JSONObject;

import java.net.URL;
import java.util.Collection;

public interface ConfigProvider {
	public boolean shouldProvideSavannahForUrl(URL url);
	public Collection<Plugin> pluginsForUrl(URL url);
	public JSONObject settingsForUrl(URL url);
}

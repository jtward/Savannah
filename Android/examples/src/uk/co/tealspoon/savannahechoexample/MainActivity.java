package uk.co.tealspoon.savannahechoexample;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONObject;

import java.net.URL;
import java.util.Collection;

import uk.co.tealspoon.savannah.ConfigProvider;
import uk.co.tealspoon.savannah.Plugin;
import uk.co.tealspoon.savannah.WebViewManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
		
		WebView echoWebView = (WebView) findViewById(R.id.echo_web_view);
		echoWebView.getSettings().setJavaScriptEnabled(true);
		
		final ArrayList<Plugin> plugins = new ArrayList<Plugin>(1);
		plugins.add(new EchoPlugin());
		
		final JSONObject settings = new JSONObject();
		try {
			settings.put("foo", "bar");
		}
		catch (Exception e) {
		}

		URL url = null;
		try {
			url = new URL("file:///android_asset/www/index.html");
		}
		catch (MalformedURLException e) {
		}
		
		WebViewManager manager = new WebViewManager("main", echoWebView, this, settings, plugins, url);
	}
}

package uk.co.tealspoon.savannahechoexample;

import java.util.ArrayList;

import org.json.JSONObject;

import uk.co.tealspoon.savannah.Plugin;
import uk.co.tealspoon.savannah.WebViewManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
		
		ArrayList<Plugin> plugins = new ArrayList<Plugin>(1);
		plugins.add(new EchoPlugin());
		
		JSONObject settings = new JSONObject();
		try {
			settings.put("foo", "bar");
		}
		catch (Exception e) {
		}
		
		WebViewManager manager = new WebViewManager("main", echoWebView, this, settings, plugins,
				"file:///android_asset/www/index.html");
	}

}

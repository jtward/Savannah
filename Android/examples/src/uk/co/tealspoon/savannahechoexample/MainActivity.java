package uk.co.tealspoon.savannahechoexample;

import java.util.ArrayList;

import org.json.JSONObject;

import uk.co.tealspoon.savannah.Plugin;
import uk.co.tealspoon.savannah.WebViewManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		WebView echoWebView = (WebView) findViewById(R.id.echo_web_view);
		echoWebView.getSettings().setJavaScriptEnabled(true);
		
		ArrayList<Plugin> plugins = new ArrayList<Plugin>(1);
		plugins.add(new EchoPlugin());
		
		JSONObject settings = new JSONObject();
		try {
			settings.put("foo", "bar");
		
		WebViewManager manager = new WebViewManager("main", echoWebView, this, settings, plugins,
				"file:///android_asset/www/index.html");
		
		manager.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String loadedUrl) {
				Log.d("SAVANNAH", "Load finished for page " + loadedUrl + "!");
			}
		});
		}
		catch (Exception e) {
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

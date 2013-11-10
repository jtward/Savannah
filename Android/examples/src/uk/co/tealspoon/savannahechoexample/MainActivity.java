package uk.co.tealspoon.savannahechoexample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		WebView echoWebView = (WebView) findViewById(R.id.echo_web_view);
		echoWebView.getSettings().setJavaScriptEnabled(true);
		
		ArrayList<SavannahPlugin> plugins = new ArrayList<SavannahPlugin>(1);
		plugins.add(new EchoPlugin());
		
		SavannahWebViewManager manager = new SavannahWebViewManager(echoWebView, plugins, "file:///android_asset/www/index.html");
		
		manager.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String loadedUrl) {
				Log.d("SAVANNAH", "Load finished for page " + loadedUrl + "!");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

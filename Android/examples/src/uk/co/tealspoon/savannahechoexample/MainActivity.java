package uk.co.tealspoon.savannahechoexample;

import java.util.ArrayList;

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
		
		final WebView echoWebView = (WebView) findViewById(R.id.echo_web_view);
		WebSettings webSettings = echoWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		SavannahWebViewManager manager = new SavannahWebViewManager();
		ArrayList<SavannahPlugin> plugins = new ArrayList<SavannahPlugin>(1);
		EchoPlugin echoPlugin = new EchoPlugin();
		plugins.add(echoPlugin);
		manager.manageWebView(echoWebView, "file:///android_asset/www/index.html", plugins);
		
		echoWebView.loadUrl("file:///android_asset/www/index.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

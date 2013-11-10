package uk.co.tealspoon.savannahechoexample;

import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SavannahWebViewManager {

	private WebView webView;
	private HashMap<String, SavannahPlugin> plugins;
	
	private class WebViewJavascriptInterface {
		private SavannahWebViewManager manager;

		public WebViewJavascriptInterface(SavannahWebViewManager manager) {
			this.manager = manager;
		}
		
		@JavascriptInterface
		public String exec(String command) {
			manager.handleCommand(command);
			return "";
		}
	}
	
	public SavannahWebViewManager(final WebView webView, Collection<SavannahPlugin> plugins, final String url) {
		
		this.webView = webView;
		
		int initialCapacity = (plugins == null) ? 0 : plugins.size();
		
		this.plugins = new HashMap<String, SavannahPlugin>(initialCapacity);
		
		if (initialCapacity > 0) { 
			for(SavannahPlugin p : plugins) {
				this.plugins.put(p.getName(), p);
			}
		}
		
		this.webView.addJavascriptInterface(new WebViewJavascriptInterface(this), "savannahJSI");
		
		this.webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String loadedUrl) {
				if (loadedUrl.equals(url)) {
					webView.loadUrl("javascript:window.savannah.didFinishLoad();");
				}
			}
		});
		
		this.webView.loadUrl(url);
	}

	public void handleCommand(final String cmd) {
		Log.d("SAVANNAH", "HANDLING COMMAND " + cmd);
		try {
			JSONArray args = new JSONArray(cmd);
			String pluginName = args.getString(1);
			String methodName = args.getString(2);
			
			SavannahPlugin plugin = plugins.get(pluginName);
			if (plugin == null) {
				Log.d("Savannah", "Plugin " + pluginName + " not found");
			}
			else {
				Log.d("SAVANNAH", "args is " + args.getString(3));
				SavannahCommand command = new SavannahCommand(args.getString(3), args.getInt(0), pluginName, methodName, this, webView);
				plugin.execute(methodName, command.arguments, command);
			}
			
		} catch (JSONException e) {
			Log.d("Savannah", "Malformed JSON: " + cmd);
		}
		
	}
	
	public void registerPlugin(SavannahPlugin plugin) {
		this.plugins.put(plugin.getName(), plugin);
	}

	public void sendPluginResult(SavannahPluginResult result, final WebView webView, int callbackId) {
		String arguments = result.argumentsAsJSON();
		String status = Boolean.toString(result.status);
	    boolean keepCallback = result.keepCallback;
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	    	final String execString = "window.savannah.nativeCallback('" + callbackId + "'," + status + "," + arguments + "," + keepCallback + ");";
		    
		    ((Activity)webView.getContext()).runOnUiThread(new Runnable() {
		    	public void run() {
		    		webView.evaluateJavascript(execString, null);
		    	}
		    });
	    }
	    else {
		    final String execString = "javascript:window.savannah.nativeCallback('" + callbackId + "'," + status + "," + arguments + "," + keepCallback + ");";
		    
		    ((Activity)webView.getContext()).runOnUiThread(new Runnable() {
		    	public void run() {
		    		webView.loadUrl(execString);
		    	}
		    });
	    }
	}
}

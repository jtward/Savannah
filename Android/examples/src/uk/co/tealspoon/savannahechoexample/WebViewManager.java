package uk.co.tealspoon.savannahechoexample;

import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewManager {

	private String name;
	private WebView webView;
	private HashMap<String, Plugin> plugins;
	private WebViewClient webViewClient;
	private Activity activity;
	
	private class WebViewJavascriptInterface {
		private WebViewManager manager;

		public WebViewJavascriptInterface(WebViewManager manager) {
			this.manager = manager;
		}
		
		@JavascriptInterface
		public String exec(String command) {
			manager.handleCommands(command);
			return "";
		}
	}
	
	public WebViewManager(String name, final WebView webView, final Activity activity, Collection<Plugin> plugins, final String url) {
		
		this.name = name;
		this.webView = webView;
		this.activity = activity;
		
		int initialCapacity = (plugins == null) ? 0 : plugins.size();
		
		this.plugins = new HashMap<String, Plugin>(initialCapacity);
		
		if (initialCapacity > 0) { 
			for(Plugin p : plugins) {
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
				if (webViewClient != null) {
					webViewClient.onPageFinished(view, loadedUrl);
				}
			}
			
			// proxy all WebViewClient methods
			@Override
			public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
				if (webViewClient != null) {
					webViewClient.doUpdateVisitedHistory(view, url, isReload);
				}
			}
			
			@Override
			public void	onFormResubmission(WebView view, Message dontResend, Message resend) {
				if (webViewClient != null) {
					webViewClient.onFormResubmission(view, dontResend, resend);
				}
			}
			
			@Override
			public void onLoadResource(WebView view, String url) {
				if (webViewClient != null) {
					webViewClient.onLoadResource(view, url);
				}
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (webViewClient != null) {
					webViewClient.onPageStarted(view, url, favicon);
				}
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if (webViewClient != null) {
					webViewClient.onReceivedError(view, errorCode, description, failingUrl);
				}
			}
			
			@Override
			public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
				if (webViewClient != null) {
					webViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
				}
			}
			
			@Override
			public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
				if (webViewClient != null) {
					webViewClient.onReceivedLoginRequest(view, realm, account, args);
				}
			}
			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				if (webViewClient != null) {
					webViewClient.onReceivedSslError(view, handler, error);
				}
			}
			
			@Override
			public void onScaleChanged(WebView view, float oldScale, float newScale) {
				if (webViewClient != null) {
					webViewClient.onScaleChanged(view, oldScale, newScale);
				}
			}
			
			@Override
			public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
				if (webViewClient != null) {
					webViewClient.onUnhandledKeyEvent(view, event);
				}
			}
			
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				if (webViewClient != null) {
					return webViewClient.shouldInterceptRequest(view, url);
				}
				else {
					return null;
				}
			}
			
			@Override
			public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
				if (webViewClient != null) {
					return webViewClient.shouldOverrideKeyEvent(view, event);
				}
				else {
					return false;
				}
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (webViewClient != null) {
					return webViewClient.shouldOverrideUrlLoading(view, url);
				}
				else {
					return false;
				}
			}
		});
		
		this.webView.loadUrl(url);
	}
	
	public String getName() {
		return name;
	}

	public void handleCommands(final String commandsString) {
		try {
			JSONArray commands = new JSONArray(commandsString);
			for(int i = 0; i < commands.length(); i += 1) {
				
				JSONArray command = commands.optJSONArray(i);
				
				if (command != null) {
					int callbackId = command.getInt(0);
					String pluginName = command.getString(1);
					String methodName = command.getString(2);
					String arguments = command.getString(3);
					
					Plugin plugin = plugins.get(pluginName);
					if (plugin == null) {
						Log.d("Savannah", "Plugin " + pluginName + " not found");
					}
					else {
						try {
							JSONArray args = new JSONArray(arguments);
							Command cmd = new Command(callbackId, this, webView, activity);
							plugin.execute(methodName, args, cmd);
						}
						catch (JSONException e) {
							Log.d("Savannah", "Malformed JSON: " + arguments);
						}
					}
				}
			}
		} catch (JSONException e) {
			Log.d("Savannah", "Malformed JSON: " + commandsString);
		}
	}
	
	public void registerPlugin(Plugin plugin) {
		this.plugins.put(plugin.getName(), plugin);
	}

	public void sendPluginResult(PluginResult result, int callbackId) {
		String arguments = result.argumentsAsJSON();
		String status = Boolean.toString(result.status);
	    boolean keepCallback = result.keepCallback;
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	    	final String execString = "window.savannah.nativeCallback('" + callbackId + "'," + status + "," + arguments + "," + keepCallback + ");";
		    
		    activity.runOnUiThread(new Runnable() {
		    	public void run() {
		    		webView.evaluateJavascript(execString, null);
		    	}
		    });
	    }
	    else {
		    final String execString = "javascript:window.savannah.nativeCallback('" + callbackId + "'," + status + "," + arguments + "," + keepCallback + ");";
		    
		    activity.runOnUiThread(new Runnable() {
		    	public void run() {
		    		webView.loadUrl(execString);
		    	}
		    });
	    }
	}
	
	void setWebViewClient(WebViewClient client) {
		webViewClient = client;
	}
}

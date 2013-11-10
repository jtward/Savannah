package uk.co.tealspoon.savannahechoexample;

import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.graphics.Bitmap;
import android.view.KeyEvent;

public class SavannahWebViewManager {

	private WebView webView;
	private HashMap<String, SavannahPlugin> plugins;
	WebViewClient webViewClient;
	
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
	
	void setWebViewClient(WebViewClient client) {
		webViewClient = client;
	}
}

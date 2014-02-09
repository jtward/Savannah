package uk.co.tealspoon.savannah;

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
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A manager for a single {@link android.webkit.WebView WebView} instance. Handles communication between the web page loaded in the WebView and a collection of {@link uk.co.tealspoon.savannah.Plugin Plugins}.
 * @author james.ward
 *
 */
public class WebViewManager {

	private String name;
	private WebView webView;
	private HashMap<String, Plugin> plugins;
	private WebViewClient webViewClient;
	private Activity activity;
	
	/**
	 * Internal class for low-level communication with the WebView.
	 * @author james.ward
	 *
	 */
	private class WebViewJavascriptInterface {
		private WebViewManager manager;

		/**
		 * Creates a new interface which reports to the given WebViewManager.
		 * @param manager the WebViewManager to report commands to.
		 */
		public WebViewJavascriptInterface(WebViewManager manager) {
			this.manager = manager;
		}
		
		/**
		 * Method called by the WebView to send commands to the native application.
		 * @param commands an array of commands represented as a JSON string.
		 * @return the empty string.
		 */
		@JavascriptInterface
		public String exec(String commands) {
			manager.handleCommands(commands);
			return "";
		}
	}
	
	/**
	 * Creates a new WebViewManager which manages the given WebView. 
	 * @param name the name of this WebViewManager. Useful for identifying WebViewManagers. Uniqueness is not enforced.
	 * @param webView the WebView managed by this WebViewManager.
	 * @param activity the Activity that contains the given WebView.
	 * @param plugins a collection of Plugins to be made available to the WebView.
	 * @param url the initial URL to load into the WebView.
	 */
	public WebViewManager(String name, final WebView webView, final Activity activity,
			Collection<Plugin> plugins, final String url) {
		
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
					executeJavaScript("window.savannah.didFinishLoad();", null);
				}
				if (webViewClient != null) {
					webViewClient.onPageFinished(view, loadedUrl);
				}
			}
			
			// proxy all other WebViewClient methods
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
	
	/**
	 * Executes the given script in the WebViewManager's WebView.
	 * @param script the script to execute.
	 * @param callback a callback to be invoked when the script execution completes with the result of the execution 
	 * (if any). May be null if no notification of the result is required. This parameter is not used if the target device is running 
	 * Android Jelly Bean MR2 (18) or earlier.
	 */
	public void executeJavaScript(String script, final ValueCallback<String> callback) {
		final boolean useEval = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		final String execString = useEval ? script : ("javascript:" + script);
		
		activity.runOnUiThread(new Runnable() {
	    	public void run() {
	    		if (useEval) {
	    			webView.evaluateJavascript(execString, callback);
	    		}
	    		else {
	    			webView.loadUrl(execString);
	    		}
	    	}
	    });
	}
	
	/**
	 * Returns the name given to this WebViewManager.
	 * @return the name given to this WebViewManager.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Handle an array of commands.
	 * @param commandsString a JSON array of commands.
	 */
	private void handleCommands(final String commandsString) {
		try {
			JSONArray commands = new JSONArray(commandsString);
			for(int i = 0; i < commands.length(); i += 1) {
				
				JSONArray command = commands.optJSONArray(i);
				
				if (command != null) {
					String callbackId = command.getString(0);
					String pluginName = command.getString(1);
					String methodName = command.getString(2);
					String arguments = command.getString(3);
					
					Plugin plugin = plugins.get(pluginName);
					if (plugin == null) {
						Log.e("Savannah", "Plugin " + pluginName + " not found");
					}
					else {
						try {
							JSONArray args = new JSONArray(arguments);
							Command cmd = new Command(callbackId, this, webView, activity);
							plugin.execute(methodName, args, cmd);
						}
						catch (JSONException e) {
							Log.e("Savannah", "Malformed JSON in arguments to " + pluginName + " " + methodName + ". JSON: " + arguments);
						}
					}
				}
			}
		} catch (JSONException e) {
			Log.e("Savannah", "Malformed JSON in command batch. JSON: " + commandsString);
		}
	}
	
	/**
	 * Register an additional Plugin to be made available to the WebView.
	 * @param plugin an implementation of Plugin.
	 */
	public void registerPlugin(Plugin plugin) {
		this.plugins.put(plugin.getName(), plugin);
	}

	/**
	 * Send the result of a Plugin execution to the WebView.
	 * @param result the result to send.
	 * @param callbackId the id of the callback that should receive the result.
	 */
	protected void sendPluginResult(PluginResult result, String callbackId) {
		String status = Boolean.toString(result.getStatus());
		String message = result.getMessage();
	    boolean keepCallback = result.getKeepCallback();
	    
	    String execString = "window.savannah.nativeCallback('" + callbackId + "'," + status + "," +
	    		message + "," + keepCallback + ");";
	    
	    executeJavaScript(execString, null);
	}
	
	/**
	 * Sets the WebViewClient that will receive various notifications and requests.
	 * @param client an implementation of WebViewClient 
	 */
	public void setWebViewClient(WebViewClient client) {
		webViewClient = client;
	}
}

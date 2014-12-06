package uk.co.tealspoon.savannah;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
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
 */
public class WebViewManager {

	private Activity activity;
	private WebView webView;
	private WebViewClient webViewClient;
	private String name;
	private ConfigProvider configProvider;

	private HashMap<String, Command> pendingCommands;
	private HashMap<String, Plugin> plugins;
	private String settingsJSON;

	private URL initialUrl;
	private JSONObject initialSettings;
	private Collection<Plugin> initialPlugins;

	/**
	 * Internal class for low-level communication with the WebView.
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
			return null;
		}
	}
	
	/**
	 * Creates a new WebViewManager which manages the given WebView. 
	 * @param name the name of this WebViewManager. Useful for identifying WebViewManagers. Uniqueness is not enforced.
	 * @param activity the Activity that contains the given WebView.
	 * @param settings the settings to pass to the WebView.
	 * @param pluginsCollection a collection of Plugins to be made available to the WebView.
	 * @param url the initial URL to load into the WebView.
	 */
	public WebViewManager(@NonNull String name, @NonNull final WebView webView, @NonNull final Activity activity,
						  @NonNull JSONObject settings, @NonNull Collection<Plugin> pluginsCollection, @NonNull final URL url) {
		
		this.name = name;
		this.webView = webView;
		this.activity = activity;

		this.initialUrl = url;

		try {
			this.initialSettings = new JSONObject(settings.toString());
		}
		catch (JSONException e) {
			// impossible
		}

		this.initialPlugins = new ArrayList<Plugin>(pluginsCollection);

		init();
	}

	/**
	 * Creates a new WebViewManager which manages the given WebView.
	 * @param name the name of this WebViewManager. Useful for identifying WebViewManagers. Uniqueness is not enforced.
	 * @param activity the Activity that contains the given WebView.
	 * @param configProvider the provider to use for retrieving Savannah configuration for pages loaded by the given WebView.
	 * @param url the initial URL to load into the WebView.
	 */
	public WebViewManager(@NonNull String name, @NonNull final WebView webView, @NonNull final Activity activity,
						  @NonNull final ConfigProvider configProvider, @NonNull final URL url) {
		this.name = name;
		this.webView = webView;
		this.activity = activity;

		this.initialUrl = url;

		this.configProvider = configProvider;

		init();
	}

	/**
	 * Initialize this manager, loading the initial URL
	 */
	private void init() {
		assertConstructorArguments();

		this.webView.addJavascriptInterface(new WebViewJavascriptInterface(this), "savannahJSI");

		this.webView.setWebViewClient(createWebViewClient());

		this.webView.loadUrl(initialUrl.toString());
	}

	/**
	 * Throw illegal argument exceptions if the arguments to the constructor were not valid.
	 */
	private void assertConstructorArguments() {
		if (name == null) {
			throw new IllegalArgumentException("Name should not be null");
		}

		if (webView == null) {
			throw new IllegalArgumentException("WebView should not be null");
		}
		else if (webView.getOriginalUrl() != null) {
			throw new IllegalArgumentException("The WebView to be managed should not be loaded or loading");
		}

		if (initialUrl == null) {
			throw new IllegalArgumentException("URL should not be null");
		}
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
				// if the activity is stopped before this runs, the webview will be null
				if (webView != null) {
					if (useEval) {
						webView.evaluateJavascript(execString, callback);
					}
					else {
						webView.loadUrl(execString);
					}
				}
			}
		});
	}

	/**
	 * Reset the internal state of the manager ready to be used by a new web page
	 * @param pluginsCollection a collection of Plugins to be made available to the WebView.
	 * @param settings the settings to pass to the WebView.
	 */
	private void reset(Collection<Plugin> pluginsCollection, JSONObject settings) {
		int initialCapacity = (pluginsCollection == null) ? 0 : pluginsCollection.size();

		plugins = new HashMap<String, Plugin>(initialCapacity);

		if (initialCapacity > 0) {
			for(Plugin p : pluginsCollection) {
				plugins.put(p.getName(), p);
			}
		}

		pendingCommands = new HashMap<String, Command>();

		settingsJSON = settings == null ? "{}" : settings.toString().replace("'", "\\'");
	}

	/**
	 * Finish loading the web page by notifying it that we're ready to receive commands
	 */
	private void finishWebPageLoad() {
		JSONArray pluginMethods = new JSONArray();
		for (Plugin p : plugins.values()) {
			pluginMethods.put(new JSONArray(p.getMethods()));
		}
		executeJavaScript("window.savannah._didFinishLoad(" + settingsJSON + ", " +
				new JSONArray(plugins.keySet()).toString() + ", " +
				pluginMethods.toString() + ");", null);
	}

	/**
	 * Return a WebViewClient to handle events from the WebView
	 * @return the WebViewClient to use
	 */
	private WebViewClient createWebViewClient() {
		return new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String loadedUrlString) {

				try {
					URL loadedUrl = new URL(loadedUrlString);

					if (configProvider != null) {
						if (configProvider.shouldProvideSavannahForUrl(loadedUrl)) {
							Collection<Plugin> pluginsCollection = configProvider.pluginsForUrl(loadedUrl);
							JSONObject settings = configProvider.settingsForUrl(loadedUrl);

							reset(pluginsCollection, settings);
							finishWebPageLoad();
						}
					}
					else if (initialUrl.getProtocol().equals(loadedUrl.getProtocol()) &&
							initialUrl.getHost().equals(loadedUrl.getHost()) &&
							initialUrl.getPort() == loadedUrl.getPort() &&
							initialUrl.getPath().equals(loadedUrl.getPath())) {

						reset(initialPlugins, initialSettings);
						finishWebPageLoad();
					}
					else {
						Log.d("Savannah", "Savannah not provided for the URL " + loadedUrlString);
					}
				}
				catch (MalformedURLException e) {
					// the loaded url was malformed?!
				}
				if (webViewClient != null) {
					webViewClient.onPageFinished(view, loadedUrlString);
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
			public void	onFormResubmission(WebView view, @NonNull Message dontResend, Message resend) {
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
			public void onPageStarted(WebView view, String loadedUrl, Bitmap favicon) {
				if (webViewClient != null) {
					webViewClient.onPageStarted(view, loadedUrl, favicon);
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				if (webViewClient != null) {
					webViewClient.onReceivedError(view, errorCode, description, failingUrl);
				}
			}

			@Override
			public void onReceivedHttpAuthRequest(WebView view, @NonNull HttpAuthHandler handler, String host, String realm) {
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
			public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler, SslError error) {
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
			public void onUnhandledKeyEvent(@NonNull WebView view, @NonNull KeyEvent event) {
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
		};
	}
	
	/**
	 * Returns the name given to this WebViewManager.
	 * @return the name given to this WebViewManager.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the WebView managed by this WebViewManager.
	 * @return the WebView.
	 */
	public WebView getWebView() {
		return webView;
	}

	/**
	 * Returns the registered Plugin with the given name, or null if no Plugin with the given name is registered.
	 * @param pluginName the name of the registered Plugin to return.
	 */
	public Plugin getPlugin(String pluginName) {
		return this.plugins.get(pluginName);
	}

	/**
	 * Sets the WebViewClient that will receive various notifications and requests.
	 * @param client an implementation of WebViewClient
	 */
	public void setWebViewClient(WebViewClient client) {
		webViewClient = client;
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
							Command cmd = new Command(args, callbackId, this, activity);
							if (pendingCommands.get(callbackId) == null) {
								pendingCommands.put(callbackId, cmd);
								plugin.execute(methodName, cmd);
							}
							else {
								Log.e("Savannah", "Command with callback ID " + callbackId + " is already pending");
							}
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
	 * Send the result of a Plugin execution to the WebView.
	 * @param status the status to send.
	 * @param message the message to send.
	 * @param keepCallback true if the callback should be kept rather than discarded.
	 * @param callbackId the id of the callback that should receive the result.
	 */
	protected void sendPluginResult(boolean status, String message, boolean keepCallback, String callbackId) {
		if (pendingCommands.get(callbackId) == null) {
			Log.i("Savannah", "Command with callback ID " + callbackId + " is not pending. This could be because the page was unloaded. Ignoring.");
			return;
		}

		if (!keepCallback) {
			pendingCommands.remove(callbackId);
		}

		String statusString = Boolean.toString(status);

		String execString = "window.savannah._callback('" + callbackId + "'," + statusString + "," +
				message + "," + keepCallback + ");";

		executeJavaScript(execString, null);
	}
}

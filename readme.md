Savannah
===================

Savannah is a web-native bridge for hybrid apps with a plugin architecture, similar to Apache Cordova.

It is designed to be easy to drop into native apps, and enables you to use multiple isolated webviews with their own set of plugins.

## Differences between Savannah and Cordova / Phonegap

- You can create as many Savannah-managed webviews as you like. Each webview has its own manager, and is isolated from all others. Plugins are configurable per-URL per-manager, which means each URL loaded in each webview can be given access to a different set of plugins. You control which plugin instances are provided each time a new URL is loaded.

- Savannah is intentionally lightweight, giving you, the developer, as much control as possible. You should expect to write native code when using Savannah in your apps.

- The native plugin APIs are deliberately similar to Cordova's, to ease transitions, but there are several notable differences intended to make writing plugins simpler, and more consistent between platforms. For example, there are no plugin result codes, just success, error and progress, and plugins can't include any JavaScript, which makes things much simpler for plugin consumers. Other differences are down to Savannah's multiple-webview model.

- Exactly the same tiny savannah.js file for both iOS and Android, which means you can concat and minify it in with the rest of your JavaScript.

- No JavaScript events (pause, resume, et cetera).

- Sending JavaScript typed arrays across the native bridge is not currently supported.

- No XML or JSON config files, core plugins, or CLIs. Just plain JavaScript and native code. Savannah is not a platform.

Docs
===================

## iOS

To use Savannah:

```Objective-C
// create a WKWebView. The code for UIWebView is the same but uses SVNHLegacyWebViewManager
WKWebView *webView = [WKWebView new];

// create a SVNHWebViewManager and pass in a name, the webview, settings, plugins and the url to load into the webview
self.webViewManager = [[SVNHWebViewManager alloc] initWithName:@"main"
                                                       WebView:webView
                                                      settings:@{}
                                                       plugins:@[[MyPlugin new]]
                                                           URL:[NSURL fileURLWithPath:[[NSBundle mainBundle]
                                                            pathForResource:@"www/index"
                                                                     ofType:@"html"]]];
```

This will create a manager that will bind itself to the page whenever it matches the given URL.
If you use a config provider, you can tell the manager to bind to other URLs and specify the settings and plugins to be loaded for those URLs:

```Objective-C
// create a config provider:
@interface MyConfigProvider : NSObject <SVNHConfigProvider>
@end
@implementation MyConfigProvider
- (BOOL) shouldProvideSavannahForURL:(NSURL *)url {
  // don't do this in real code: check the URL!
  return YES;
}

- (NSArray *) pluginsForURL:(NSURL *)url {
  return @[[SVNHEchoPlugin new]];
}

- (NSDictionary *) settingsForURL:(NSURL *)url {
  return @{@"foo": @"bar"};
}
@end
```
```Objective-C
// create a WKWebView. The code for UIWebView is the same but uses SVNHLegacyWebViewManager
WKWebView *webView = [WKWebView new];

self.webViewManager = [[SVNHWebViewManager alloc] initWithName:@""
                                                       webView:webView
                                                configProvider:[MyConfigProvider new]
                                                           URL:[NSURL fileURLWithPath:[[NSBundle mainBundle]
                                                            pathForResource:@"www/index"
                                                                     ofType:@"html"]]];
```

Don't forget to include the savannah.js file in your web page!


A plugin class just implements SVNHPlugin. You need to implement an `execute` method, similar to Cordova's Android implementation, and a `getMethods` method to expose the plugin's methods.

```Objective-C
@interface MyPlugin : NSObject <SVNHPlugin>
+ (NSString *) name;
+ (NSArray *) methods;
- (BOOL) execute:(NSString *)action
     withCommand:(SVNHCommand *)command;
@end
```

```Objective-C
@implementation MyPlugin

+ (NSString *) name {
    return @"com.example.foo";
};

+ (NSString *) methods {
    return @[@"bar"];
};

- (BOOL) execute:(NSString *)action
     withCommand:(SVNHCommand *)command {

  // check for the `bar` action
  if ([action isEqualToString @"bar"]) {
    // report success and pass back the string "bar!"
    [command successWithString:@"bar!"];
    return YES;
  }
  return NO;
}
```


## Android

To use Savannah:

```Java
// create or get a webview
WebView webView = (WebView) findViewById(R.id.web_view);

// make sure JavaScript is enabled in the webview
webView.getSettings().setJavaScriptEnabled(true);

// create a list for the plugins and add the plugins
ArrayList<Plugin> plugins = new ArrayList<Plugin>(1);
plugins.add(new MyPlugin());

// create a JSON object which contains whatever data needs to be passed to the webview
JSONObject settings = new JSONObject();

// create a WebViewManager and pass in a name, the webview, settings, plugins and the url to load into the webview
new WebViewManager("main", webView, settings, plugins, new URL("file:///android_asset/www/index.html"));
```

This will create a manager that will bind itself to the page whenever it matches the given URL.
If you use a config provider, you can tell the manager to bind to other URLs and specify the settings and plugins to be loaded for those URLs:

```Java
// create or get a webview
WebView webView = (WebView) findViewById(R.id.web_view);

// make sure JavaScript is enabled in the webview
webView.getSettings().setJavaScriptEnabled(true);

// create a config provider to specify the settings and plugins to be loaded for given URLs:
ConfigProvider configProvider = new ConfigProvider() {
  @Override
  public boolean shouldProvideSavannahForUrl(URL url) {
    // don't do this in real code: check the URL!
    return true;
  }

  @Override
  public Collection<Plugin> pluginsForUrl(URL url) {
    ArrayList<Plugin> plugins = new ArrayList<Plugin>(1);
    plugins.add(new MyPlugin());
    return plugins;
  }

  @Override
  public JSONObject settingsForUrl(URL url) {
    return new JSONObject();
  }
};

// create a WebViewManager and pass in a name, the webview, a config provider and the url to load into the webview
new WebViewManager("main", webView, configProvider, new URL("file:///android_asset/www/index.html"));
```

Don't forget to include the savannah.js file in your web page!

A plugin class just implements `Plugin`. You need to implement an `execute` method, similar to Cordova, and a `getMethods` method to expose the plugin's methods.

```Java
public class MyPlugin implements SavannahPlugin {

  @Override
  public String getName() {
    return "com.example.foo";
  }

  @Override
  public Collection<String> getMethods() {
    return Arrays.asList("bar");
  }

  @Override
  public boolean execute(String action, Command command) {

    // check for the `bar` action
    if (action.equals("bar")) {
      // report success and pass back the string "bar!"
      command.success("bar!");
      return true;
    }

    return false;
  }
}
```

## JavaScript

There are two main ways to call plugin methods from JavaScript:

### Plugin execution via savannah.plugins
When plugins are registered from the native app, they are made available on `savannah.plugins` by name with their methods. Because plugins' real names can be quite verbose, you can tell Savannah to alias them. You can only use this method with promises, not callbacks:

```JavaScript
savannah.alias({
  "com.example.foo": "foo"
});

// call the bar method on the foo plugin with one argument, "baz"
savannah.plugins.foo.bar("baz")
  .progress(function(result) {})
  .then(function(result) {})
  .catch(function(error) {});
```

Be careful when using `progress`: it is added by Savannah and not part of the promises spec, and is therefore not available when chained after `then` or `catch`.

### Plugin execution via savannah.exec
This method lets you use exactly the same syntax with `savannah.exec` as you would use with `cordova.exec`:

```JavaScript
savannah.exec(function success(result) {}, // success callback
  function error(error) {},                // error callback
  "com.example.foo",                       // plugin identifier / name
  "bar",                                   // plugin method
  ["baz"]);                                // plugin arguments
```

You can also use this method with promises:
```JavaScript
savannah.exec("com.example.foo",        // plugin identifier / name
  "bar",                                // plugin method
  ["baz"])                              // plugin arguments
  .progress(function(result) {})
  .then(function(result) {})
  .catch(function(error) {});
```

Savannah uses either promises or callbacks, but not both. For example, if you pass callbacks to `savannah.exec`, a promise will not be returned. Savannah.js depends on `window.Promise` or a polyfill, and you must wait for the `savannah.ready` promise to resolve before calling plugin methods.

## Roadmap
- Generated documentation
- Add tests
- Create Pod and Jar files

## Changelog
### 0.13.0, 11th December 2014
- Savannah for iOS now supports WKWebView. SVNHWebViewManager now manages a WKWebView; UIWebViews are now managed by the new SVNHLegacyWebViewManager class. In order to keep the manager APIs consistent, `executeJavaScript` now takes a block instead of returning the result directly.

### 0.12.0, 6th December 2014
- Savannah can now reconnect after page loads. The plugins and settings for the new page are configurable using a config provider.
- On Android, the manager constructor now takes a URL object rather than a string.

### 0.11.0, 24th November 2014
- Added the option to pass a default value to all argument retrieval methods.
- An error is now thrown in savannah.js if a plugin alias would be overridden.

### 0.10.0, 12th November 2014
- Changed the way arguments are retrieved from Commands by Plugins. The iOS and Android Command APIs are now more consistent.

### 0.9.0, 1st November 2014
- Made changes to Plugin and Command syntax in both Android and iOS to be more consistent with each other rather than Cordova. iOS plugins must now implement execute, and the plugin arguments are now accessed via the command on Android.

### 0.8.0, 11th June 2014
- Native plugins must now provide a list of the methods they support.
- Plugins on savannah.plugins are now a hash of the plugin's methods, rather than just a single method. Those methods are called with separate arguments, rather than an arguments array.

### 0.7.0, 10th June 2014
- Plugin execution from savannah.js is now debounced for better efficiency.
- Removed onDeviceReady callback and added a ready promise in savannah.js.
- savannah.js now depends on window.Promise or a polyfill.

### 0.6.0, 8th June 2014
- Made currently available plugins visible in the webview.
- Now use only window.Promise. 

### 0.5.0, 19th May 2014
- Added promise support to savannah.exec.
- Added progress methods to Command, and removed keepCallback arguments from success and error methods in favour of a deprecated Cordova-style setKeepCallback method.

### 0.4.0, 18th May 2014
- Unified savannah.js: the same JS file is now used across both iOS and Android.

### 0.3.0, 9th May 2014
- Added settings to managers, which get passed to the webview.
- Added an optional onDeviceReady callback, which gets called when settings are known.

### 0.2.0, 8th February 2014
- Added methods for getting and removing plugins from managers.
- Fixed a bug in Android where a null pointer exception could occur when executing JavaScript on the WebView after it had been destroyed.

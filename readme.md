Savannah
===================

Savannah is a web-native bridge for hybrid apps with a plugin architecture, similar to Apache Cordova.

It is designed to be easy to drop into native apps, and enables you to use multiple isolated webviews with their own set of plugins.

Version 0.8.0 - 11th June 2014

Version 0.7.0 - 10th June 2014

Version 0.6.0 - 8th June 2014

Version 0.5.0 - 19th May 2014

Version 0.4.0 - 18th May 2014

Version 0.3.0 - 9th May 2014

Version 0.2.0 - 23rd March 2014

Version 0.1.0 - 8th February 2014

## Differences between Savannah and Cordova / Phonegap

- You can create as many Savannah-managed webviews as you like. Each webview has its own manager, and is isolated from all others. Plugins are per-manager, which means your webviews can have different plugins available to them. You can use the same instance of a plugin in multiple managers, or create a new instance for each manager.

- Savannah is intentionally lightweight, giving you, the developer, as much control as possible. You should expect to write native code when using Savannah in your apps.

- The native plugin APIs are deliberately similar to Cordova's, to ease transitions, but there are several notable differences intended to make writing plugins simpler, and more consistent between platforms. For example, there are no plugin result codes, just success and error, and JavaScript in plugins is not supported. Other differences are down to Savannah's multiple-webview model.

- No JavaScript events (pause, resume, et cetera).

- Exactly the same tiny savannah.js file for both iOS and Android, which means you can concat and minify it in with the rest of your JavaScript.

- Sending JavaScript typed arrays across the native bridge is not currently supported.

- No XML or JSON config files, core plugins, or CLIs. Just plain JavaScript and native code. Savannah is not a platform.

Docs
===================

## iOS

To use Savannah:

```Objective-C
// create a webview
UIWebView *webView = [UIWebView new];

// create a SVNHWebViewManager and pass in a name, the webview, settings, plugins and the url to load into the webview
self.webViewManager = [[SVNHWebViewManager alloc] initWithName:@"main"
                                                       WebView:webView
                                                      settings:@{}
                                                       plugins:@[[MyPlugin new]]
                                                           URL:[NSURL fileURLWithPath:[[NSBundle mainBundle]
                                                            pathForResource:@"www/index"
                                                                     ofType:@"html"]]];
                                                      }
```

Don't forget to include the savannah.js file in your web page!


A plugin class just implements SVNHPlugin. Plugin methods take a SVNHCommand as their only argument.

```Objective-C
@interface MyPlugin : NSObject <SVNHPlugin>
+ (NSString *) name;
+ (NSArray *) methods;
- (void) foo:(SVNHCommand *)command;
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

- (void) bar:(SVNHCommand *)command {
    // report success and pass back the string "bar!"
    [command successWithString:@"bar!"];
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
new WebViewManager("main", webView, settings, plugins, "file:///android_asset/www/index.html");
```

Don't forget to include the savannah.js file in your web page!

A plugin class just implements `Plugin`. You need to implement an `execute` method, similar to Cordova.

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
  public boolean execute(String action, JSONArray args, Command command) {

    // check for the `bar` action
    if(action.equals("bar")) {
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

Savannah user either promises or callbacks, but not both. If you pass callbacks to `savannah.exec`, a promise will not be returned. Savannah.js depends on `window.Promise` or a polyfill, and you must wait for the `savannah.ready` promise to resolve before calling plugin methods.

## Changelog
### 0.8.0
- Native plugins must now provide a list of the methods they support.
- Plugins on savannah.plugins are now a hash of the plugin's methods, rather than just a single method. Those methods are called with separate arguments, rather than an arguments array.

### 0.7.0
- Plugin execution from savannah.js is now debounced for better efficiency.
- Removed onDeviceReady callback and added a ready promise in savannah.js.
- savannah.js now depends on window.Promise or a polyfill.

### 0.6.0
- Made currently available plugins visible in the webview.
- Now use only window.Promise. 

### 0.5.0
- Added promise support to savannah.exec.
- Added progress methods to Command, and removed keepCallback arguments from success and error methods in favour of a deprecated Cordova-style setKeepCallback method.

### 0.4.0
- Unified savannah.js: the same JS file is now used across both iOS and Android.

### 0.3.0
- Added settings to managers, which get passed to the webview.
- Added an optional onDeviceReady callback, which gets called when settings are known.

### 0.2.0
- Added methods for getting and removing plugins from managers.
- Fixed a bug in Android where a null pointer exception could occur when executing JavaScript on the WebView after it had been destroyed.
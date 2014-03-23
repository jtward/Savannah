Savannah
===================

Savannah is a web-native bridge for hybrid apps with a plugin architecture, similar to Apache Cordova.

It is designed to be easy to drop into native apps, and enables you to use multiple isolated webviews with their own set of plugins.

Version 0.2.0 - 23rd March 2014

Version 0.1.0 - 8th February 2014

## Differences between Savannah and Cordova / Phonegap

- You can create as many Savannah-managed webviews as you like. Each webview has its own manager, and is isolated from all others. Plugins are per-manager, which means your webviews can have different plugins available to them. You can use the same instance of a plugin in multiple managers, or create a new instance for each manager.

- Savannah is intentionally lightweight, giving you, the developer, as much control as possible. You should expect to write native code when using Savannah in your apps.

- The native plugin APIs are deliberately similar to Cordova's, to ease transitions, but there are several notable differences intended to make writing plugins simpler, and more consistent between platforms. For example, there are no plugin result codes, just success and error. Other differences are down to Savannah's multiple-webview model.

- No JavaScript events (pause, resume, et cetera).

- Sending JavaScript typed arrays across the native bridge is not currently supported.

- No XML or JSON config files, core plugins, or CLIs. Just plain JavaScript and native code. Savannah is not a platform.

Docs
===================

## iOS

To use Savannah:

```Objective-C
// create a webview
UIWebView *webView = [UIWebView new];

// create a SVNHWebViewManager and pass in a name, the webview, plugins and the url to load into the webview
self.webViewManager = [[SVNHWebViewManager alloc] initWithName:@"main"
                                                       WebView:webView
                                                       plugins:@[[MyPlugin new]]
                                                           URL:[NSURL fileURLWithPath:[[NSBundle mainBundle]
                                                            pathForResource:@"www/index"
                                                                     ofType:@"html"]]];
```

Don't forget to include the iOS savannah.js file in your web page!


A plugin class just implements SVNHPlugin. Plugin methods take a SVNHCommand as their only argument.

```Objective-C
@interface MyPlugin : NSObject <SVNHPlugin>
+ (NSString *) name;
- (void) foo:(SVNHCommand *)command;
@end
```

```Objective-C
@implementation MyPlugin
+ (NSString *) name {
    return @"com.example.foo";
};

- (void) foo:(SVNHCommand *)command {
    // report success and pass back the string "foo!"
    // we don't need to return more than one response so return NO for keepCallback
    [command successWithMessageAsString:@"foo!" keepCallback:NO];
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

// create a WebViewManager and pass in a name, the webview, plugins and the url to load into the webview
new WebViewManager("main", webView, plugins, "file:///android_asset/www/index.html");

```

Don't forget to include the Android savannah.js file in your web page!

A plugin class just implements `Plugin`. You need to implement an `execute` method, similar to Cordova, and a `getName` method.

```Java
public class MyPlugin implements SavannahPlugin {

  @Override
  public String getName() {
    return "com.example.foo";
  }

  @Override
  public boolean execute(String action, JSONArray args, Command command) {

    // check for the `foo` action
    if(action.equals("foo")) {
      // report success and pass back the string "foo!"
      // we don't need to return more than one response so return NO for keepCallback
      command.success("foo!", false);
      return true;
    }

    return false;
  }
}
```


## JavaScript

To execute a plugin from JavaScript, just call `savannah.exec`, in exactly the same way you would call `Cordova.exec`.

```JavaScript
savannah.exec(function success(result) {}, // success callback
  function error(error) {},                // error callback
  "com.example.foo",                       // plugin identifier / name
  "foo",                                   // plugin method
  []);                                     // plugin arguments
```


## Changelog
### 0.2.0
- Added methods for getting and removing plugins from managers.
- Fixed a bug in Android where a null pointer exception could occur when executing JavaScript on the WebView after it had been destroyed.
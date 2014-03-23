#import "SVNHPlugin.h"

/*!
 * A manager for a single UIWebView instance. Handles communication between the web page loaded in the UIWebView and a collection of Plugins.
 */
@interface SVNHWebViewManager : NSObject <UIWebViewDelegate>

/*!
 * Returns the name given to this WebViewManager.
 * @return the name given to this WebViewManager.
 */
@property (nonatomic, readonly) NSString *name;

@property (nonatomic, readonly) UIWebView *webView;

@property (nonatomic) id<UIWebViewDelegate> delegate;

/*!
 * Creates a new WebViewManager which manages the given WebView.
 * @param name the name of this WebViewManager. Useful for identifying WebViewManagers. Uniqueness is not enforced.
 * @param webView the WebView managed by this WebViewManager.
 * @param plugins a collection of Plugins to be made available to the WebView.
 * @param URL the initial URL to load into the WebView.
 */
- (id) initWithName:(NSString *)name
            WebView:(UIWebView *)webView
            plugins:(NSArray *)plugins
                URL:(NSURL *)URL;


/*!
 * Registers a plugin to be made available to the WebView.
 * @param plugin a plugin to be made available to the WebView.
 */
- (void) registerPlugin:(id <SVNHPlugin>)plugin;

/*!
 * Unregisters a plugin, making it unavailable to the WebView.
 * @param pluginName the name of the plugin to unregister.
 * @discussion Does nothing if the plugin is not currently registered.
 * Any messages returned from the plugin to the WebView after the plugin is unregistered are not affected and will be passed back to the WebView as normal.
 */
- (void) unregisterPluginByName:(NSString *)pluginName;

/*!
 * Returns the registered plugin with the given name, or nil if no plugin with the given name is registered.
 * @param pluginName the name of the registered plugin to return.
 */
- (id<SVNHPlugin>) getPluginByName:(NSString *)pluginName;

/*!
 * Unregister all Plugins, making them unavailable to the WebView.
 * @discussion Any messages returned from the plugin to the WebView after the plugin is unregistered are not affected and will be passed back to the WebView as normal.
 */
- (void) clearPlugins;

/*!
 * Sends the result of a Plugin execution to the UIWebView.
 * @param result the result to send.
 * @param callbackId the id of the callback that should receive the result.
 */
- (void) sendPluginResponseWithStatus:(BOOL)status
                              message:(NSString *)message
                         keepCallback:(BOOL)keepCallback
                           callbackId:(NSString *)callbackId;

/*!
 * Executes the given script in the WebViewManager's WebView.
 * @param script the script to execute.
 * @return the result of the execution.
 */
- (NSString *) executeJavaScript:(NSString *)script;

@end

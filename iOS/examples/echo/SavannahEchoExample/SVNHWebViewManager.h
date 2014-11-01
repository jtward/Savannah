#import <UIKit/UIKit.h>
@protocol SVNHPlugin;

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
 * @param settings the settings to pass to the WebView.
 * @param plugins a collection of Plugins to be made available to the WebView.
 * @param URL the initial URL to load into the WebView.
 */
- (id) initWithName:(NSString *)name
            WebView:(UIWebView *)webView
           settings:(NSDictionary *)settings
            plugins:(NSArray *)plugins
                URL:(NSURL *)URL;

/*!
 * Returns the registered plugin with the given name, or nil if no plugin with the given name is registered.
 * @param pluginName the name of the registered plugin to return.
 */
- (id<SVNHPlugin>) getPluginByName:(NSString *)pluginName;

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

#import <WebKit/WebKit.h>
#import "SVNHBaseWebViewManager.h"

@protocol SVNHConfigProvider;

/*!
 * A manager for a single WKWebView instance. Handles communication between the web page loaded in the WKWebView and a collection of Plugins.
 */
@interface SVNHWebViewManager : SVNHBaseWebViewManager <WKNavigationDelegate>

/*!
 * Returns the WebView managed by this WebViewManager.
 * @return the WebView managed by this WebViewManager.
 */
@property (nonatomic, readonly) WKWebView *webView;

/*!
 * Returns the WKNavigationDelegate for this WebViewManager.
 * @return the WKNavigationDelegate for this WebViewManager.
 */
@property (nonatomic, weak) id<WKNavigationDelegate> navigationDelegate;

/*!
 * Creates a new WebViewManager which manages the given WebView.
 * @param name the name of this WebViewManager. Useful for identifying WebViewManagers. Uniqueness is not enforced.
 * @param webView the WebView managed by this WebViewManager.
 * @param settings the settings to pass to the WebView.
 * @param plugins a collection of Plugins to be made available to the WebView.
 * @param URL the initial URL to load into the WebView.
 */
- (id) initWithName:(NSString *)name
            webView:(WKWebView *)webView
           settings:(NSDictionary *)settings
            plugins:(NSArray *)plugins
                URL:(NSURL *)URL
__attribute__((nonnull (1, 2, 5)));

/*!
 * Creates a new WebViewManager which manages the given WebView.
 * @param name the name of this WebViewManager. Useful for identifying WebViewManagers. Uniqueness is not enforced.
 * @param webView the WebView managed by this WebViewManager.
 * @param configProvider the provider to use for retrieving Savannah configuration for pages loaded by the given WebView.
 * @param URL the initial URL to load into the WebView.
 */
- (id) initWithName:(NSString *)name
            webView:(WKWebView *)webView
     configProvider:(id <SVNHConfigProvider>)configProvider
                URL:(NSURL *)URL
__attribute__((nonnull (1, 2, 3, 4)));

@end

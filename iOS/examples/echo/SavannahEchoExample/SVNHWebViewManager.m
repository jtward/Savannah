#import "SVNHWebViewManager.h"
#import "SVNHBaseWebViewManager+Protected.h"
#import "SVNHCommand.h"
#import "SVNHPlugin.h"
#import "SVNHConfigProvider.h"

#pragma mark SavannahJSI
/*!
 * The JavaScript interface added to a page to allow communication to a webViewManager.
 * Accessible from the web page via window.webkit.messageHandlers.savannahJSI.
 */
@interface SavannahJSI : NSObject <WKScriptMessageHandler>

@property (nonatomic, weak) SVNHWebViewManager *webViewManager;
- (id)initWithWebViewManager:(SVNHWebViewManager *)webViewManager;

/*!
 * Called by the web page with an array of commands to handle
 */
- (void)userContentController:(WKUserContentController *)userContentController
      didReceiveScriptMessage:(WKScriptMessage *)message;

@end

@implementation SavannahJSI

- (id)initWithWebViewManager:(SVNHWebViewManager *)webViewManager {
    self = [super init];
    self.webViewManager = webViewManager;
    return self;
}

- (void)userContentController:(WKUserContentController *)userContentController
      didReceiveScriptMessage:(WKScriptMessage *)message {

    [self.webViewManager handleCommands:message.body];
}

@end

#pragma mark SVNHWebViewManager
@interface SVNHWebViewManager()

@property (nonatomic) WKWebView *webView;

@property (nonatomic, weak) id <SVNHConfigProvider> configProvider;

@property (nonatomic) NSMutableDictionary *pendingCommands;

@property (nonatomic) NSDictionary *plugins;
@property (nonatomic) NSString *settingsJSON;

@property (nonatomic) NSURL *initialURL;
@property (nonatomic) NSArray *initialPlugins;
@property (nonatomic) NSDictionary *initialSettings;

@end

@implementation SVNHWebViewManager

@dynamic pendingCommands;
@dynamic plugins;
@dynamic settingsJSON;

- (id) initWithName:(NSString *)name
            webView:(WKWebView *)webView
           settings:(NSDictionary *)settings
            plugins:(NSArray *)plugins
                URL:(NSURL *)URL {

    self = [super init];

    _name = name;
    self.webView = webView;

    [self.webView.configuration.userContentController addScriptMessageHandler:[[SavannahJSI alloc] initWithWebViewManager:self]
                                                                         name:@"savannahJSI"];

    self.initialURL = [URL copy];
    self.initialPlugins = [plugins copy];
    self.initialSettings = [settings copy];

    NSAssert(self.webView.loading == NO && self.webView.estimatedProgress == 0.0,
             @"The WKWebView to be managed should not be loaded or loading");

    [self.webView setNavigationDelegate:self];
    [self.webView loadRequest:[NSURLRequest requestWithURL:self.initialURL]];

    return self;
}

- (id) initWithName:(NSString *)name
            webView:(WKWebView *)webView
     configProvider:(id <SVNHConfigProvider>)configProvider
                URL:(NSURL *)URL {

    self = [super init];

    _name = name;
    self.webView = webView;
    self.initialURL = [URL copy];
    self.configProvider = configProvider;

    NSAssert(self.webView.loading == NO && self.webView.estimatedProgress == 0.0,
             @"The WKWebView to be managed should not be loaded or loading");

    [self.webView setNavigationDelegate:self];
    [self.webView loadRequest:[NSURLRequest requestWithURL:self.initialURL]];
    
    return self;
}

- (void) setDelegate:(id <UIWebViewDelegate>)delegate {
    self.delegate = delegate;
}

- (void)webView:(WKWebView *)webView
didCommitNavigation:(WKNavigation *)navigation {

    if (self.navigationDelegate != nil &&
        [self.navigationDelegate respondsToSelector:@selector(webView:didCommitNavigation:)]) {

        return [self.navigationDelegate webView:webView
                            didCommitNavigation:navigation];
    }
}

- (void)webView:(WKWebView *)webView
didFailNavigation:(WKNavigation *)navigation
        withError:(NSError *)error {

    if (self.navigationDelegate != nil &&
        [self.navigationDelegate respondsToSelector:@selector(webView:didFailNavigation:withError:)]) {

        return [self.navigationDelegate webView:webView
                              didFailNavigation:navigation
                                      withError:error];
    }
}

- (void)webView:(WKWebView *)webView
    didFailProvisionalNavigation:(WKNavigation *)navigation
                       withError:(NSError *)error {

    if (self.navigationDelegate != nil &&
        [self.navigationDelegate respondsToSelector:@selector(webView:didFailProvisionalNavigation:withError:)]) {

        return [self.navigationDelegate webView:webView
                   didFailProvisionalNavigation:navigation
                                      withError:error];
    }
}

- (void)webView:(WKWebView *)webView
didFinishNavigation:(WKNavigation *)navigation {

    NSURL *url = self.webView.URL;

    if (self.configProvider != nil) {
        if ([self.configProvider shouldProvideSavannahForURL:url]) {
            NSArray *plugins = [self.configProvider pluginsForURL:url];
            NSDictionary *settings = [self.configProvider settingsForURL:url];

            [self resetWithPlugins:plugins
                          settings:settings];

            [self finishWebviewLoad];
        }
        else {
            NSLog(@"Config provider does not allow Savannah to be provided for the %@", [url absoluteString]);
        }
    }

    else if ([self.initialURL.scheme isEqualToString:url.scheme] &&
             ([self.initialURL.host isEqualToString:url.host] || (self.initialURL.host == url.host)) &&
             self.initialURL.port == url.port &&
             ([self.initialURL.path isEqualToString:url.path] || (self.initialURL.path == url.path))) {

        [self resetWithPlugins:self.initialPlugins
                      settings:self.initialSettings];

        [self finishWebviewLoad];
    }
    else {
        NSLog(@"Savannah not provided for the URL %@", [url absoluteString]);
    }

    if (self.navigationDelegate != nil &&
        [self.navigationDelegate respondsToSelector:@selector(webView:didFinishNavigation:)]) {

        return [self.navigationDelegate webView:webView
                            didFinishNavigation:navigation];
    }
}

- (void)webView:(WKWebView *)webView
didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
                completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition disposition,
                            NSURLCredential *credential))completionHandler {

    if (self.navigationDelegate != nil &&
        [self.navigationDelegate respondsToSelector:@selector(webView:didReceiveAuthenticationChallenge:completionHandler:)]) {

        return [self.navigationDelegate webView:webView
              didReceiveAuthenticationChallenge:challenge
                              completionHandler:completionHandler];
    }
}

- (void)webView:(WKWebView *)webView
didReceiveServerRedirectForProvisionalNavigation:(WKNavigation *)navigation {

    if (self.navigationDelegate != nil &&
        [self.navigationDelegate respondsToSelector:@selector(webView:didReceiveServerRedirectForProvisionalNavigation:)]) {

        return [self.navigationDelegate webView:webView
didReceiveServerRedirectForProvisionalNavigation:navigation];
    }
}

- (void)webView:(WKWebView *)webView
didStartProvisionalNavigation:(WKNavigation *)navigation {

    if (self.navigationDelegate != nil &&
        [self.navigationDelegate respondsToSelector:@selector(webView:didStartProvisionalNavigation:)]) {

        return [self.navigationDelegate webView:webView
                  didStartProvisionalNavigation:navigation];
    }
}

- (void) executeJavaScript:(NSString *)script
         completionHandler:(void (^)(id, NSError *))completionHandler {
    
    [self.webView evaluateJavaScript:script
                   completionHandler:completionHandler];
}

@end

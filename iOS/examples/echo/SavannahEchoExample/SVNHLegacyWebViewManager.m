#import "SVNHLegacyWebViewManager.h"
#import "SVNHBaseWebViewManager+Protected.h"
#import "SVNHPlugin.h"
#import "SVNHConfigProvider.h"

@interface SVNHLegacyWebViewManager()

@property (nonatomic) UIWebView *webView;

@property (nonatomic, weak) id <SVNHConfigProvider> configProvider;

@property (nonatomic) NSMutableDictionary *pendingCommands;
@property (nonatomic) NSDictionary *plugins;
@property (nonatomic) NSString *settingsJSON;

@property (nonatomic) NSURL *initialURL;
@property (nonatomic) NSArray *initialPlugins;
@property (nonatomic) NSDictionary *initialSettings;

@end

@implementation SVNHLegacyWebViewManager

@dynamic pendingCommands;
@dynamic plugins;
@dynamic settingsJSON;

- (id) initWithName:(NSString *)name
            webView:(UIWebView *)webView
           settings:(NSDictionary *)settings
            plugins:(NSArray *)plugins
                URL:(NSURL *)URL {

    self = [super init];

    _name = name;
    self.webView = webView;
    self.initialURL = [URL copy];
    self.initialPlugins = [plugins copy];
    self.initialSettings = [settings copy];

    NSAssert(self.webView.request == nil, @"The UIWebView to be managed should not be loaded or loading");

    [self.webView setDelegate:self];
    [self.webView loadRequest:[NSURLRequest requestWithURL:self.initialURL]];

    return self;
}

- (id) initWithName:(NSString *)name
            webView:(UIWebView *)webView
     configProvider:(id <SVNHConfigProvider>)configProvider
                URL:(NSURL *)URL {

    self = [super init];

    _name = name;
    self.webView = webView;
    self.initialURL = [URL copy];
    self.configProvider = configProvider;

    NSAssert(self.webView.request == nil, @"The UIWebView to be managed should not be loaded or loading");

    [self.webView setDelegate:self];
    [self.webView loadRequest:[NSURLRequest requestWithURL:self.initialURL]];
    
    return self;
}

- (BOOL) webView:(UIWebView *)theWebView
shouldStartLoadWithRequest:(NSURLRequest *)request
  navigationType:(UIWebViewNavigationType)navigationType {

    if ([request.URL.path isEqualToString:@"/!svnh_exec"]) {
        [self executeJavaScript:@"window.savannah._fetchMessages();"
              completionHandler:^(id cmdsId, NSError *scriptError) {
                  NSString *cmds = (NSString *)cmdsId;
                  NSError *error;
                  NSArray *cmdsArray = [NSJSONSerialization JSONObjectWithData:[cmds dataUsingEncoding:NSUTF8StringEncoding]
                                                                       options:0
                                                                         error:&error];

                  [self handleCommands:cmdsArray];
              }];

        return NO;
    }
    else if (self.delegate != nil &&
             [self.delegate respondsToSelector:@selector(webView:shouldStartLoadWithRequest:navigationType:)]) {

        // forward on to the user's delegate
        return [self.delegate webView:self.webView
           shouldStartLoadWithRequest:request
                       navigationType:navigationType];
    }
    else {
        return YES;
    }
}

- (void) webView:(UIWebView *)webView
didFailLoadWithError:(NSError *)error {
    if (self.delegate != nil
        && [self.delegate respondsToSelector:@selector(webView:didFailLoadWithError:)]) {

        [self.delegate webView:webView didFailLoadWithError:error];
    }
}

- (void) webViewDidStartLoad:(UIWebView *)theWebView {
    if (self.delegate != nil &&
        [self.delegate respondsToSelector:@selector(webViewDidStartLoad:)]) {

        [self.delegate webViewDidStartLoad:self.webView];
    }
}

- (void) webViewDidFinishLoad:(UIWebView *)theWebView {
    [self executeJavaScript:@"window.savannah && !window.savannah._getIsLoadFinished() && window.location.href;"
          completionHandler:^(id locationId, NSError *error) {
        NSString *location = (NSString *)locationId;

              if (![location isEqualToString:@""]) {
                  NSURL *url = [[NSURL alloc] initWithString:location];

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
              }
              else {
                  [self executeJavaScript:@"window.location.href;"
                        completionHandler:^(id hrefId, NSError *error) {
                            NSLog(@"savannah.js is not loaded by the web page at %@", hrefId);
                        }];
              }
              if (self.delegate != nil &&
                  [self.delegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {

                  [self.delegate webViewDidFinishLoad:self.webView];
              }

          }];
}

- (void) finishWebviewLoad {
    NSData *pluginNamesData = [NSJSONSerialization dataWithJSONObject:[self.plugins allKeys]
                                                              options:0
                                                                error:nil];

    NSString *pluginNamesJSON = [[NSString alloc] initWithData:pluginNamesData
                                                      encoding:NSUTF8StringEncoding];

    NSMutableArray *pluginMethods = [[NSMutableArray alloc] initWithCapacity:[self.plugins count]];

    for (id <SVNHPlugin> plugin in [self.plugins allValues]) {
        [pluginMethods addObject:[[plugin class] methods]];
    }

    NSData *pluginMethodsData = [NSJSONSerialization dataWithJSONObject:pluginMethods
                                                                options:0
                                                                  error:nil];

    NSString *pluginMethodsJSON = [[NSString alloc] initWithData:pluginMethodsData
                                                        encoding:NSUTF8StringEncoding];

    [self executeJavaScript:[NSString stringWithFormat:@"window.savannah._didFinishLoad(%@, %@, %@);",
                             self.settingsJSON,
                             pluginNamesJSON,
                             pluginMethodsJSON]
          completionHandler:nil];
}

- (void) executeJavaScript:(NSString *)script
         completionHandler:(void (^)(id, NSError *))completionHandler {
    
    NSString *result = [self.webView stringByEvaluatingJavaScriptFromString:script];
    if (completionHandler != nil) {
        completionHandler(result, nil);
    }
}

@end
#import "SVNHWebViewManager.h"
#import "SVNHCommand.h"
#import "SVNHPlugin.h"
#import "SVNHConfigProvider.h"

@interface SVNHWebViewManager()

@property (nonatomic) UIWebView *webView;
@property (nonatomic) NSString *name;
@property (nonatomic) id <SVNHConfigProvider> configProvider;

@property (nonatomic) NSMutableDictionary *pendingCommands;

@property (nonatomic) NSDictionary *plugins;
@property (nonatomic) NSString *settingsJSON;

@property (nonatomic) NSURL *initialURL;
@property (nonatomic) NSArray *initialPlugins;
@property (nonatomic) NSDictionary *initialSettings;

@end

@implementation SVNHWebViewManager

- (id) initWithName:(NSString *)name
            webView:(UIWebView *)webView
           settings:(NSDictionary *)settings
            plugins:(NSArray *)plugins
                URL:(NSURL *)URL {

    self = [super init];

    self.name = name;
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

    self.name = name;
    self.webView = webView;
    self.initialURL = [URL copy];
    self.configProvider = configProvider;

    NSAssert(self.webView.request == nil, @"The UIWebView to be managed should not be loaded or loading");

    [self.webView setDelegate:self];
    [self.webView loadRequest:[NSURLRequest requestWithURL:self.initialURL]];

    return self;
}

- (void) resetWithPlugins:(NSArray *)plugins
                 settings:(NSDictionary *)settings {

    long pluginsCount = plugins == nil ? 0 : [plugins count];

    NSMutableDictionary *pluginsDictionary = [[NSMutableDictionary alloc] initWithCapacity:pluginsCount];

    for (id <SVNHPlugin> plugin in plugins) {

        [pluginsDictionary setObject:plugin
                              forKey:[[plugin class] name]];
    }

    self.plugins = [pluginsDictionary copy];

    self.pendingCommands = [NSMutableDictionary new];

    settings = settings ?: [NSDictionary new];

    NSData* settingsData = [NSJSONSerialization dataWithJSONObject:settings
                                                           options:0
                                                             error:nil];

    self.settingsJSON = [[NSString alloc] initWithData:settingsData
                                              encoding:NSUTF8StringEncoding];
}


- (void) setDelegate:(id <UIWebViewDelegate>)delegate {
    self.delegate = delegate;
}

- (NSString *) executeJavaScript:(NSString *)script {
    return [self.webView stringByEvaluatingJavaScriptFromString:script];
}

- (id <SVNHPlugin>) getPluginByName:(NSString *)pluginName {
    return [self.plugins objectForKey:pluginName];
}

- (BOOL) webView:(UIWebView *)theWebView
shouldStartLoadWithRequest:(NSURLRequest *)request
  navigationType:(UIWebViewNavigationType)navigationType {

    if ([request.URL.path isEqualToString:@"/!svnh_exec"]) {
        NSString *cmds = [self executeJavaScript:@"window.savannah._fetchMessages();"];
        NSError *error;
        NSArray *cmdsArray = [NSJSONSerialization JSONObjectWithData:[cmds dataUsingEncoding:NSUTF8StringEncoding]
                                                             options:0
                                                               error:&error];
        if (error != nil) {
            NSLog(@"Malformed JSON in command batch. JSON: %@", cmds);
        }
        else {
            for(NSArray *cmd in cmdsArray) {

                NSString *pluginName = [cmd objectAtIndex:1];
                NSString *methodName = [cmd objectAtIndex:2];

                id <SVNHPlugin> plugin = [self.plugins objectForKey:pluginName];

                if (plugin == nil) {
                    NSLog(@"Plugin %@ not found", pluginName);
                }
                else {
                    NSString *callbackId = [cmd objectAtIndex:0];
                    SVNHCommand *command = [[SVNHCommand alloc] initWithArguments:[cmd objectAtIndex:3]
                                                                       callbackId:callbackId
                                                                   webViewManager:self];

                    if ([self.pendingCommands objectForKey:callbackId] == nil) {
                        [self.pendingCommands setObject:command forKey:callbackId];
                        [plugin execute:methodName withCommand:command];
                    }
                    else {
                        NSLog(@"Command with callback ID %@ is already pending", callbackId);
                    }
                }
            }
        }
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

    NSString *location = [self executeJavaScript:@"window.savannah && !window.savannah._getIsLoadFinished() && window.location.href;"];
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
        location = [self executeJavaScript:@"window.location.href;"];
        NSLog(@"savannah.js is not loaded by the web page at %@", location);
    }
    if (self.delegate != nil &&
        [self.delegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {
        
        [self.delegate webViewDidFinishLoad:self.webView];
    }
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
                             pluginMethodsJSON]];
}

- (void) sendPluginResponseWithStatus:(BOOL)status
                              message:(NSString *)message
                         keepCallback:(BOOL)keepCallback
                           callbackId:(NSString *)callbackId {

    if ([self.pendingCommands objectForKey:callbackId] == nil) {
        NSLog(@"Command with callback ID %@ is not pending", callbackId);
        return;
    }

    if (!keepCallback) {
        [self.pendingCommands removeObjectForKey:callbackId];
    }

    NSString *stringStatus = status ? @"true" : @"false";

    NSString *execString = [NSString stringWithFormat:@"window.savannah._callback('%@',%@,%@,%d);",
                            callbackId,
                            stringStatus,
                            message,
                            keepCallback];

    [self executeJavaScript:execString];
}

@end

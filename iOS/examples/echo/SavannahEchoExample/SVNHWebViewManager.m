#import "SVNHWebViewManager.h"
#import "SVNHCommand.h"

@interface SVNHWebViewManager()

@property (nonatomic) NSMutableDictionary *plugins;
@property (nonatomic) BOOL isFirstRequest;
@property (nonatomic) BOOL isFirstLoad;
@property (nonatomic) UIWebView *webView;
@property (nonatomic) NSString *name;
@property (nonatomic) NSString *settingsJSON;

@end

@implementation SVNHWebViewManager

- (id) initWithName:(NSString *)name
            WebView:(UIWebView *)webView
           settings:(NSDictionary *)settings
            plugins:(NSArray *)plugins
                URL:(NSURL *)URL {
    
    self = [super init];
    
    self.isFirstRequest = YES;
    self.isFirstLoad = YES;
    
    self.name = name;
    self.webView = webView;
    
    int pluginsCount = plugins == nil ? 0 : [plugins count];
    
    NSMutableDictionary *pluginsDictionary = [[NSMutableDictionary alloc] initWithCapacity:pluginsCount];

    for (id <SVNHPlugin> plugin in plugins) {
        
        [pluginsDictionary setObject:plugin
                              forKey:[[plugin class] name]];
    }
    
    self.plugins = pluginsDictionary;
    
    if (settings == nil) {
        settings = [NSDictionary new];
    }
    
    NSData* settingsData = [NSJSONSerialization dataWithJSONObject:settings
                                                           options:0
                                                             error:nil];
    
    NSString *settingsJSON = [[NSString alloc] initWithData:settingsData
                                                   encoding:NSUTF8StringEncoding];
    
    self.settingsJSON = [settingsJSON substringWithRange:NSMakeRange(0, [settingsJSON length])];
    
    [self.webView setDelegate:self];
    [self.webView loadRequest:[NSURLRequest requestWithURL:URL]];
    
    return self;
}

- (void) setDelegate:(id<UIWebViewDelegate>)delegate {
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
    
    if (self.isFirstRequest) {
        self.isFirstRequest = NO;
        return YES;
    }
    else if ([[request.URL path] isEqualToString:@"/!svnh_exec"]) {
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
                    SEL execSelector = NSSelectorFromString([NSString stringWithFormat:@"%@:",
                                                             methodName]);
                    
                    if ([plugin respondsToSelector:execSelector]) {
                        
                        // create command and send
                        SVNHCommand *command = [[SVNHCommand alloc] initWithArguments:[cmd objectAtIndex:3]
                                                                           callbackId:[cmd objectAtIndex:0]
                                                                       webViewManager:self
                                                                              webView:theWebView];
                        
                        
                        [plugin performSelector:execSelector
                                     withObject:command];
                    }
                    else {
                        NSLog(@"Plugin %@ has no method %@", pluginName, methodName);
                    }
                }
            }
        }
        return NO;
    }
    else {
        // forward on to the user's delegate
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"webView:shouldStartLoadWithRequest:navigationType:"]);
        if (self.delegate != nil && [self.delegate respondsToSelector:selector]) {
            NSMethodSignature *signature;
            NSInvocation *invocation;
            signature = [[self.delegate class] instanceMethodSignatureForSelector:selector];
            invocation = [NSInvocation invocationWithMethodSignature:signature];
            [invocation setSelector:selector];
            [invocation setTarget:self.delegate];
            
            UIWebView *theWebView = self.webView;
            
            [invocation setArgument:&theWebView
                            atIndex:2];
            
            [invocation setArgument:&request
                            atIndex:3];
            
            [invocation setArgument:&navigationType
                            atIndex:4];
            
            [invocation invoke];
            
            BOOL result;
            [invocation getReturnValue:&result];
            
            return result;
        }
        return YES;
    }
}

- (void) webView:(UIWebView *)webView
    didFailLoadWithError:(NSError *)error {
    
    if (self.delegate != nil) {
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"webView:didFailLoadWithError:"]);
        
        if ([self.delegate respondsToSelector:selector]) {
            
            [self.delegate performSelector:selector withObject:webView
                                withObject:error];
        }
    }
}

- (void) webViewDidFinishLoad:(UIWebView *)theWebView {
    
    if (self.isFirstLoad) {
        
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
        
        [self executeJavaScript:[NSString stringWithFormat:@"window.savannah._didFinishLoad(%@, %@, %@);", self.settingsJSON, pluginNamesJSON, pluginMethodsJSON]];
        
        self.isFirstLoad = NO;
    }
    if (self.delegate != nil) {
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"webViewDidFinishLoad:"]);
        
        if ([self.delegate respondsToSelector:selector]) {
            
            [self.delegate performSelector:selector
                                withObject:self.webView];
        }
    }
}

- (void) webViewDidStartLoad:(UIWebView *)theWebView {
    
    if (self.delegate != nil) {
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"webViewDidStartLoad:"]);
        
        if ([self.delegate respondsToSelector:selector]) {
            
            [self.delegate performSelector:selector
                                withObject:self.webView];
        }
    }
}

- (void) sendPluginResponseWithStatus:(BOOL)status
                              message:(NSString *)message
                         keepCallback:(BOOL)keepCallback
                           callbackId:(NSString *)callbackId {
    
    NSString *stringStatus = status ? @"true" : @"false";
    
    NSString *execString = [NSString stringWithFormat:@"window.savannah._callback('%@',%@,%@,%d);",
                            callbackId,
                            stringStatus,
                            message,
                            keepCallback];
    
    [self executeJavaScript:execString];
}

@end

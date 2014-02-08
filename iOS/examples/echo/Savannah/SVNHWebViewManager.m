#import "SVNHWebViewManager.h"
#import "SVNHCommand.h"
#import "SVNHPlugin.h"

@interface SVNHWebViewManager()

@property (nonatomic) NSMutableDictionary *plugins;
@property (nonatomic) BOOL isFirstRequest;
@property (nonatomic) BOOL isFirstLoad;
@property (nonatomic) id<UIWebViewDelegate> delegate;
@property (nonatomic) UIWebView *webView;
@property (nonatomic) NSString *name;

@end

@implementation SVNHWebViewManager

- (id) initWithName:(NSString *)name WebView:(UIWebView *)theWebView plugins:(NSArray *)plugins URL:(NSURL *)URL {
    self = [super init];
    
    self.name = name;
    self.webView = theWebView;
    [self.webView setDelegate:self];
    
    NSMutableDictionary *pluginsDictionary = [[NSMutableDictionary alloc] initWithCapacity:(plugins == nil ? 0 : [plugins count])];
    for (id <SVNHPlugin> plugin in plugins) {
        [pluginsDictionary setObject:plugin forKey:[[plugin class] name]];
    }
    
    self.plugins = pluginsDictionary;
    self.isFirstRequest = YES;
    self.isFirstLoad = YES;
    
    NSURLRequest* appReq = [NSURLRequest requestWithURL:URL];
    [self.webView loadRequest:appReq];
    
    return self;
}

// allow plugins to be registered to a webview after initializing the delegate
- (void) registerPlugin:(id <SVNHPlugin>)plugin {
    [self.plugins setObject:plugin forKey:[plugin name]];
}

- (BOOL)webView:(UIWebView *)theWebView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (self.isFirstRequest) {
        self.isFirstRequest = NO;
        return YES;
    }
    else if ([[request.URL path] isEqualToString:@"/!svnh_exec"]) {
        NSString *cmds = [theWebView stringByEvaluatingJavaScriptFromString:@"window.savannah.nativeFetchMessages()"];
        NSError *error;
        NSArray *cmdsArray = [NSJSONSerialization JSONObjectWithData:[cmds dataUsingEncoding:NSUTF8StringEncoding] options:0 error:&error];
        if (error != nil) {
            NSLog(@"Malformed JSON: %@", cmds);
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
                    SEL execSelector = NSSelectorFromString([NSString stringWithFormat:@"%@:",methodName]);
                    if ([plugin respondsToSelector:execSelector]) {
                        // create command and send
                        SVNHCommand *command = [[SVNHCommand alloc] initWithArguments:[cmd objectAtIndex:3] callbackId:[cmd objectAtIndex:0] webViewManager:self webView:theWebView];
                        [plugin performSelector:execSelector withObject:command];
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
            [invocation setArgument:&theWebView atIndex:2];
            [invocation setArgument:&request atIndex:3];
            [invocation setArgument:&navigationType atIndex:4];
            
            [invocation invoke];
            BOOL result;
            [invocation getReturnValue:&result];
            return result;
        }
        return YES;
    }
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    if (self.delegate != nil) {
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"webView:didFailLoadWithError:"]);
        if ([self.delegate respondsToSelector:selector]) {
            [self.delegate performSelector:selector withObject:webView withObject:error];
        }
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)theWebView {
    if (self.isFirstLoad) {
        [theWebView stringByEvaluatingJavaScriptFromString:@"window.savannah.didFinishLoad()"];
        self.isFirstLoad = NO;
    }
    if (self.delegate != nil) {
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"webViewDidFinishLoad:"]);
        if ([self.delegate respondsToSelector:selector]) {
            [self.delegate performSelector:selector withObject:self.webView];
        }
    }
}

- (void)webViewDidStartLoad:(UIWebView *)theWebView {
    if (self.delegate != nil) {
        SEL selector = NSSelectorFromString([NSString stringWithFormat:@"webViewDidStartLoad:"]);
        if ([self.delegate respondsToSelector:selector]) {
            [self.delegate performSelector:selector withObject:self.webView];
        }
    }
}

- (void) sendPluginResult:(SVNHPluginResult *)result withCallbackId:(NSString *)callbackId {
    NSString *arguments = result.message;
    NSString *status = result.status ? @"true" : @"false";
    BOOL keepCallback = result.keepCallback;
    NSString *execString = [NSString stringWithFormat:@"window.savannah.nativeCallback('%@',%@,%@,%d);", callbackId, status, arguments, keepCallback];
    [self.webView stringByEvaluatingJavaScriptFromString:execString];
}

@end

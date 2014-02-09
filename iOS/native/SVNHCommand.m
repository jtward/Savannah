#import "SVNHCommand.h"
#import "SVNHWebViewManager.h"

@interface SVNHCommand()

@property (nonatomic) NSArray* arguments;
@property (nonatomic) NSString* callbackId;
@property (nonatomic) SVNHWebViewManager* webViewManager;
@property (nonatomic) UIWebView* webView;

@end

@implementation SVNHCommand

- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
          webViewManager:(SVNHWebViewManager *)webViewManager
                 webView:(UIWebView *)webView {
    
    self = [super init];
    
    self.arguments = arguments;
    self.callbackId = callbackId;
    self.webViewManager = webViewManager;
    self.webView = webView;
    
    return self;
}

- (id) argumentAtIndex:(NSUInteger)index {
    
    return [self argumentAtIndex:index
                     withDefault:nil];
}

- (id) argumentAtIndex:(NSUInteger)index
           withDefault:(id)defaultValue {
    
    return [self argumentAtIndex:index
                     withDefault:defaultValue
                        andClass:nil];
}

- (id) argumentAtIndex:(NSUInteger)index
           withDefault:(id)defaultValue
              andClass:(Class)aClass {
    
    if (index >= [_arguments count]) {
        return defaultValue;
    }
    id ret = [_arguments objectAtIndex:index];
    if (ret == [NSNull null]) {
        ret = defaultValue;
    }
    if ((aClass != nil) && ![ret isKindOfClass:aClass]) {
        ret = defaultValue;
    }
    return ret;
}

- (void)sendPluginResult:(SVNHPluginResult *)result {
    [self.webViewManager sendPluginResult:result
                           withCallbackId:self.callbackId];
}

- (void) successAndKeepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES
                                                        keepCallback:keepCallback
                                                             message:nil]];
}

- (void) successWithMessageAsArray:(NSArray *)message
                      keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES
                                                        keepCallback:keepCallback
                                                             message:message]];
}

- (void) successWithMessageAsBool:(BOOL)message
                     keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES
                                                        keepCallback:keepCallback
                                                             message:[NSNumber numberWithBool:message]]];
}

- (void) successWithMessageAsDictionary:(NSDictionary *)message
                           keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES
                                                        keepCallback:keepCallback
                                                             message:message]];
}

- (void) successWithMessageAsDouble:(double)message
                       keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES
                                                        keepCallback:keepCallback
                                                             message:[NSNumber numberWithInt:message]]];
}

- (void) successWithMessageAsInt:(int)message
                    keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES
                                                        keepCallback:keepCallback
                                                             message:[NSNumber numberWithDouble:message]]];
}

- (void) successWithMessageAsString:(NSString *)message
                       keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES
                                                        keepCallback:keepCallback
                                                             message:message]];
}

- (void) errorAndKeepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO
                                                        keepCallback:keepCallback
                                                             message:nil]];
}

- (void) errorWithMessageAsArray:(NSArray *)message
                    keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO
                                                        keepCallback:keepCallback
                                                             message:message]];
}

- (void) errorWithMessageAsBool:(BOOL)message
                   keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO
                                                        keepCallback:keepCallback
                                                             message:[NSNumber numberWithBool:message]]];
}

- (void) errorWithMessageAsDictionary:(NSDictionary *)message
                         keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO
                                                        keepCallback:keepCallback
                                                             message:message]];
}

- (void) errorWithMessageAsDouble:(double)message
                     keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO
                                                        keepCallback:keepCallback
                                                             message:[NSNumber numberWithInt:message]]];
}

- (void) errorWithMessageAsInt:(int)message
                  keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO
                                                        keepCallback:keepCallback
                                                             message:[NSNumber numberWithDouble:message]]];
}

- (void) errorWithMessageAsString:(NSString *)message
                     keepCallback:(BOOL)keepCallback {
    
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO
                                                        keepCallback:keepCallback
                                                             message:message]];
}

@end

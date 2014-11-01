#import "SVNHCommand.h"
#import "SVNHWebViewManager.h"

@interface SVNHCommand()

@property (nonatomic) NSArray* arguments;
@property (nonatomic) NSString* callbackId;
@property (nonatomic) SVNHWebViewManager* webViewManager;
@property (nonatomic) NSString* webViewManagerName;
@property (nonatomic) UIWebView* webView;
@property (nonatomic) BOOL isDiscarded;

@end

@implementation SVNHCommand

- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
          webViewManager:(SVNHWebViewManager *)webViewManager
                 webView:(UIWebView *)webView {
    
    self = [super init];
    
    self.keepCallback = NO;
    self.isDiscarded = NO;
    self.arguments = arguments;
    self.callbackId = callbackId;
    self.webViewManager = webViewManager;
    self.webView = webView;
    self.webViewManagerName = webViewManager.name;
    
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

- (void) sendPluginResultWithSuccess:(BOOL)success
                        keepCallback:(BOOL)keepCallback
                             message:(id)messageObject {
    
    if (!self.isDiscarded) {
        self.keepCallback = keepCallback;
        
        if (!self.keepCallback) {
            self.isDiscarded = YES;
        }
        
        [self.webViewManager sendPluginResponseWithStatus:success
                                                  message:[self messageAsJSON:messageObject]
                                             keepCallback:self.keepCallback
                                               callbackId:self.callbackId];
    }
    else {
        NSLog(@"Response not sent because callbacks have already been discarded.");
    }
}

- (void) success {
    [self sendPluginResultWithSuccess:YES
                         keepCallback:self.keepCallback
                              message:nil];
}

- (void) successWithArray:(NSArray *)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:self.keepCallback
                              message:message];
}

- (void) successWithBool:(BOOL)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:self.keepCallback
                              message:[NSNumber numberWithBool:message]];
}

- (void) successWithDictionary:(NSDictionary *)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:self.keepCallback
                              message:message];
}

- (void) successWithDouble:(double)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:self.keepCallback
                              message:[NSNumber numberWithInt:message]];
}

- (void) successWithInt:(int)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:self.keepCallback
                              message:[NSNumber numberWithDouble:message]];
}

- (void) successWithString:(NSString *)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:self.keepCallback
                              message:message];
}

- (void) error {
    
    [self sendPluginResultWithSuccess:NO
                         keepCallback:self.keepCallback
                              message:nil];
}

- (void) errorWithArray:(NSArray *)message {
    
    [self sendPluginResultWithSuccess:NO
                         keepCallback:self.keepCallback
                              message:message];
}

- (void) errorWithBool:(BOOL)message {
    
    [self sendPluginResultWithSuccess:NO
                         keepCallback:self.keepCallback
                              message:[NSNumber numberWithBool:message]];
}

- (void) errorWithDictionary:(NSDictionary *)message {
    
    [self sendPluginResultWithSuccess:NO
                         keepCallback:self.keepCallback
                              message:message];
}

- (void) errorWithDouble:(double)message {
    
    [self sendPluginResultWithSuccess:NO
                         keepCallback:self.keepCallback
                              message:[NSNumber numberWithInt:message]];
}

- (void) errorWithInt:(int)message {
    
    [self sendPluginResultWithSuccess:NO
                         keepCallback:self.keepCallback
                              message:[NSNumber numberWithDouble:message]];
}

- (void) errorWithString:(NSString *)message {
    
    [self sendPluginResultWithSuccess:NO
                         keepCallback:self.keepCallback
                              message:message];
}

- (void) progress {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:YES
                              message:nil];
}

- (void) progressWithArray:(NSArray *)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:YES
                              message:message];
}

- (void) progressWithBool:(BOOL)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:YES
                              message:[NSNumber numberWithBool:message]];
}

- (void) progressWithDictionary:(NSDictionary *)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:YES
                              message:message];
}

- (void) progressWithDouble:(double)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:YES
                              message:[NSNumber numberWithInt:message]];
}

- (void) progressWithInt:(int)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:YES
                              message:[NSNumber numberWithDouble:message]];
}

- (void) progressWithString:(NSString *)message {
    
    [self sendPluginResultWithSuccess:YES
                         keepCallback:YES
                              message:message];
}

- (NSString*) messageAsJSON:(id)messageObject {
    
    if (messageObject == nil) {
        messageObject = [NSNull null];
    }
    
    NSData* messageData = [NSJSONSerialization dataWithJSONObject:[NSArray arrayWithObject:messageObject]
                                                          options:0
                                                            error:nil];
    
    NSString *messageJSON = [[NSString alloc] initWithData:messageData
                                                  encoding:NSUTF8StringEncoding];
    
    return [messageJSON substringWithRange:NSMakeRange(1, [messageJSON length] - 2)];
}

@end

#import "SVNHCommand.h"
#import "SVNHWebViewManager.h"

@interface SVNHCommand()

@property (nonatomic) NSArray* arguments;
@property (nonatomic) NSString* callbackId;
@property (nonatomic) SVNHWebViewManager* webViewManager;
@property (nonatomic) NSString* webViewManagerName;
@property (nonatomic) BOOL isDiscarded;

@end

@implementation SVNHCommand

- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
          webViewManager:(SVNHWebViewManager *)webViewManager {
    
    self = [super init];
    
    self.isDiscarded = NO;
    self.arguments = arguments;
    self.callbackId = callbackId;
    self.webViewManager = webViewManager;
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
                             message:(NSString *)message
                        keepCallback:(BOOL)keepCallback {
    
    if (!self.isDiscarded) {

        if (!keepCallback) {
            self.isDiscarded = YES;
        }
        
        [self.webViewManager sendPluginResponseWithStatus:success
                                                  message:message
                                             keepCallback:keepCallback
                                               callbackId:self.callbackId];
    }
    else {
        NSLog(@"Response not sent because callbacks have already been discarded.");
    }
}

- (void) success {
    [self sendPluginResultWithSuccess:YES
                              message:nil
                         keepCallback:NO];
}

- (void) successWithArray:(NSArray *)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) successWithBool:(BOOL)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithBool:message]]
                         keepCallback:NO];
}

- (void) successWithDictionary:(NSDictionary *)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) successWithDouble:(double)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithInt:message]]
                         keepCallback:NO];
}

- (void) successWithInt:(int)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithDouble:message]]
                         keepCallback:NO];
}

- (void) successWithString:(NSString *)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) error {
    
    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:nil]
                         keepCallback:NO];
}

- (void) errorWithArray:(NSArray *)message {
    
    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) errorWithBool:(BOOL)message {
    
    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:[NSNumber numberWithBool:message]]
                         keepCallback:NO];
}

- (void) errorWithDictionary:(NSDictionary *)message {
    
    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) errorWithDouble:(double)message {
    
    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:[NSNumber numberWithInt:message]]
                         keepCallback:NO];
}

- (void) errorWithInt:(int)message {
    
    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:[NSNumber numberWithDouble:message]]
                         keepCallback:NO];
}

- (void) errorWithString:(NSString *)message {
    
    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) progress {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:nil]
                         keepCallback:YES];
}

- (void) progressWithArray:(NSArray *)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:YES];
}

- (void) progressWithBool:(BOOL)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithBool:message]]
                         keepCallback:YES];
}

- (void) progressWithDictionary:(NSDictionary *)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:YES];
}

- (void) progressWithDouble:(double)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithInt:message]]
                         keepCallback:YES];
}

- (void) progressWithInt:(int)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithDouble:message]]
                         keepCallback:YES];
}

- (void) progressWithString:(NSString *)message {
    
    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:YES];
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

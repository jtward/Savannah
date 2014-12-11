#import "SVNHCommand.h"
#import "SVNHBaseWebViewManager.h"
#import "SVNHBaseWebViewManager+Protected.h"

@interface SVNHCommand()

@property (nonatomic) NSArray* arguments;
@property (nonatomic) NSString* callbackId;
@property (nonatomic) SVNHBaseWebViewManager* webViewManager;
@property (nonatomic) NSString* webViewManagerName;
@property (nonatomic) BOOL isDiscarded;

@end

@implementation SVNHCommand

- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
          webViewManager:(SVNHBaseWebViewManager *)webViewManager {

    self = [super init];

    self.isDiscarded = NO;
    self.arguments = arguments;
    self.callbackId = callbackId;
    self.webViewManager = webViewManager;
    self.webViewManagerName = webViewManager.name;

    return self;
}

- (long) argumentsCount {
    return [self.arguments count];
}

- (BOOL) hasArrayAtIndex:(NSUInteger)index {
    if (index >= [self.arguments count]) {
        return NO;
    }
    return [[self.arguments objectAtIndex:index] isKindOfClass:[NSArray class]];
}

- (NSArray *) arrayAtIndex:(NSUInteger)index {
    if ([self hasArrayAtIndex:index]) {
        return [self.arguments objectAtIndex:index];
    }
    else {
        return nil;
    }
}

- (NSArray *) arrayAtIndex:(NSUInteger)index
              defaultValue:(NSArray *)defaultValue {
    if ([self hasArrayAtIndex:index]) {
        return [self.arguments objectAtIndex:index];
    }
    else {
        return defaultValue;
    }
}

- (BOOL) hasBoolAtIndex:(NSUInteger)index {
    if (index >= [self.arguments count]) {
        return NO;
    }

    NSNumber *argument = [self.arguments objectAtIndex:index];
    if ([argument isKindOfClass:[NSNumber class]]) {
        int value = [argument intValue];
        return value == 0 || value == 1;
    }
    else {
        return false;
    }
}

- (BOOL) boolAtIndex:(NSUInteger)index {
    if ([self hasBoolAtIndex:index]) {
        return [[self.arguments objectAtIndex:index] boolValue];
    }

    else {
        return NO;
    }
}

- (BOOL) boolAtIndex:(NSUInteger)index
           defaultValue:(BOOL)defaultValue {
    if ([self hasBoolAtIndex:index]) {
        return [[self.arguments objectAtIndex:index] boolValue];
    }
    else {
        return defaultValue;
    }
}

- (BOOL) hasDictionaryAtIndex:(NSUInteger)index {
    if (index >= [self.arguments count]) {
        return NO;
    }

    return [[self.arguments objectAtIndex:index] isKindOfClass:[NSDictionary class]];
}

- (NSDictionary *) dictionaryAtIndex:(NSUInteger)index {
    if ([self hasDictionaryAtIndex:index]) {
        return [self.arguments objectAtIndex:index];
    }
    else {
        return nil;
    }
}

- (NSDictionary *) dictionaryAtIndex:(NSUInteger)index
                        defaultValue:(NSDictionary *)defaultValue {
    if ([self hasDictionaryAtIndex:index]) {
        return [self.arguments objectAtIndex:index];
    }
    else {
        return defaultValue;
    }
}

- (BOOL) hasDoubleAtIndex:(NSUInteger)index {
    if (index >= [self.arguments count]) {
        return NO;
    }

    return [[self.arguments objectAtIndex:index] isKindOfClass:[NSNumber class]];
}

- (double) doubleAtIndex:(NSUInteger)index {
    if ([self hasDoubleAtIndex:index]) {
        return [[self.arguments objectAtIndex:index] doubleValue];
    }
    else {
        return 0;
    }
}

- (double) doubleAtIndex:(NSUInteger)index
            defaultValue:(double)defaultValue {
    if ([self hasDoubleAtIndex:index]) {
        return [[self.arguments objectAtIndex:index] doubleValue];
    }
    else {
        return defaultValue;
    }
}

- (BOOL) hasIntAtIndex:(NSUInteger)index {
    if (index >= [self.arguments count]) {
        return NO;
    }

    id value = [self.arguments objectAtIndex:index];

    if ([value isKindOfClass:[NSNumber class]]) {
        return ([value intValue] == [value doubleValue]);
    }
    else {
        return NO;
    }
}

- (int) intAtIndex:(NSUInteger)index {
    if ([self hasIntAtIndex:index]) {
        return [[self.arguments objectAtIndex:index] intValue];
    }
    else {
        return 0;
    }
}

- (int) intAtIndex:(NSUInteger)index
         defaultValue:(int)defaultValue {
    if ([self hasIntAtIndex:index]) {
        return [[self.arguments objectAtIndex:index] intValue];
    }
    else {
        return defaultValue;
    }
}

- (BOOL) hasStringAtIndex:(NSUInteger)index {
    if (index >= [self.arguments count]) {
        return NO;
    }

    return [[self.arguments objectAtIndex:index] isKindOfClass:[NSString class]];
}

- (NSString *) stringAtIndex:(NSUInteger)index {
    if ([self hasStringAtIndex:index]) {
        return [self.arguments objectAtIndex:index];
    }
    else {
        return nil;
    }
}

- (NSString *) stringAtIndex:(NSUInteger)index
                defaultValue:(NSString *)defaultValue {
    if ([self hasStringAtIndex:index]) {
        return [self.arguments objectAtIndex:index];
    }
    else {
        return defaultValue;
    }
}

- (BOOL) hasNilAtIndex:(NSUInteger)index {
    if (index >= [self.arguments count]) {
        return YES;
    }

    return [self.arguments objectAtIndex:index] == [NSNull null];
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
                              message:[self messageAsJSON:[NSNumber numberWithDouble:message]]
                         keepCallback:NO];
}

- (void) successWithInt:(int)message {

    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithInt:message]]
                         keepCallback:NO];
}

- (void) successWithString:(NSString *)message {

    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) error {

    [self sendPluginResultWithSuccess:NO
                              message:nil
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
                              message:[self messageAsJSON:[NSNumber numberWithDouble:message]]
                         keepCallback:NO];
}

- (void) errorWithInt:(int)message {

    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:[NSNumber numberWithInt:message]]
                         keepCallback:NO];
}

- (void) errorWithString:(NSString *)message {

    [self sendPluginResultWithSuccess:NO
                              message:[self messageAsJSON:message]
                         keepCallback:NO];
}

- (void) progress {

    [self sendPluginResultWithSuccess:YES
                              message:nil
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
                              message:[self messageAsJSON:[NSNumber numberWithDouble:message]]
                         keepCallback:YES];
}

- (void) progressWithInt:(int)message {

    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:[NSNumber numberWithInt:message]]
                         keepCallback:YES];
}

- (void) progressWithString:(NSString *)message {

    [self sendPluginResultWithSuccess:YES
                              message:[self messageAsJSON:message]
                         keepCallback:YES];
}

- (NSString*) messageAsJSON:(id)message {

    BOOL messageIsJSONObjectType = [message isKindOfClass:[NSArray class]] ||
    [message isKindOfClass:[NSDictionary class]];

    message = messageIsJSONObjectType ?
        message :
        [NSArray arrayWithObject:(message ?: [NSNull null])];

    NSData* messageData = [NSJSONSerialization dataWithJSONObject:message
                                                          options:0
                                                            error:nil];

    NSString *messageJSON = [[NSString alloc] initWithData:messageData
                                                  encoding:NSUTF8StringEncoding];

    return messageIsJSONObjectType ?
        messageJSON :
        [messageJSON substringWithRange:NSMakeRange(1, [messageJSON length] - 2)];
}

@end

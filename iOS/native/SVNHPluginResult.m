#import "SVNHPluginResult.h"

@interface SVNHPluginResult ()

@property (nonatomic) BOOL status;
@property (nonatomic) BOOL keepCallback;
@property (nonatomic) NSString *message;

@end

@implementation SVNHPluginResult

static NSArray* CommandStatusMsgs;

- (SVNHPluginResult*) initWithSuccess:(BOOL)success
                         keepCallback:(BOOL)shouldKeepCallback
                              message:(id)messageObject {
    
    self = [super init];
    self.status = success;
    self.keepCallback = shouldKeepCallback;
    self.message = [self messageAsJSON:messageObject];
    return self;
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
@interface SVNHPluginResult : NSObject

@property (nonatomic, readonly) BOOL status;
@property (nonatomic, readonly) BOOL keepCallback;
@property (nonatomic, readonly) NSString *message;

- (SVNHPluginResult*) initWithSuccess:(BOOL)success
                         keepCallback:(BOOL)keepCallback
                              message:(id)message;

@end

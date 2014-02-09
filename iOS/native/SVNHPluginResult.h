/*!
 * A PluginResult holds the data to be sent back to the UIWebView.
 */
@interface SVNHPluginResult : NSObject

/*!
 * True if execution of the Command was successful, false otherwise.
 */
@property (nonatomic, readonly) BOOL status;

/*!
 * True if the web callback should be preserved to allow further results to be returned, false otherwise.
 */
@property (nonatomic, readonly) BOOL keepCallback;

/*!
 * The result of the Command, to send to the UIWebView.
 */
@property (nonatomic, readonly) NSString *message;

/**
 * Create a new PluginResult.
 * @param success true if execution of the Command was successful, false otherwise.
 * @param keepCallback true if the web callback should be preserved to allow further results to be returned, false otherwise.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (SVNHPluginResult*) initWithSuccess:(BOOL)success
                         keepCallback:(BOOL)keepCallback
                              message:(id)message;

@end

#import "SVNHWebViewManager.h"
#import "SVNHPluginResult.h"

@interface SVNHCommand : NSObject

@property (nonatomic, readonly) NSArray* arguments;
@property (nonatomic, readonly) SVNHWebViewManager* webViewManager;
@property (nonatomic, readonly) UIWebView* webView;

- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
          webViewManager:(SVNHWebViewManager *)webViewManager
                 webView:(UIWebView *)webView;

// Returns the argument at the given index.
// If index >= the number of arguments, returns nil.
// If the argument at the given index is NSNull, returns nil.
- (id)argumentAtIndex:(NSUInteger)index;
// Same as above, but returns defaultValue instead of nil.
- (id)argumentAtIndex:(NSUInteger)index withDefault:(id)defaultValue;
// Same as above, but returns defaultValue instead of nil, and if the argument is not of the expected class, returns defaultValue
- (id)argumentAtIndex:(NSUInteger)index withDefault:(id)defaultValue andClass:(Class)aClass;

- (void)sendPluginResult:(SVNHPluginResult *)result;

- (void) successAndKeepCallback:(BOOL)keepCallback;
- (void) successWithMessageAsArray:(NSArray *)message keepCallback:(BOOL)keepCallback;
- (void) successWithMessageAsBool:(BOOL)message keepCallback:(BOOL)keepCallback;
- (void) successWithMessageAsDictionary:(NSDictionary *)message keepCallback:(BOOL)keepCallback;
- (void) successWithMessageAsDouble:(double)message keepCallback:(BOOL)keepCallback;
- (void) successWithMessageAsInt:(int)message keepCallback:(BOOL)keepCallback;
- (void) successWithMessageAsString:(NSString *)message keepCallback:(BOOL)keepCallback;
- (void) errorAndKeepCallback:(BOOL)keepCallback;
- (void) errorWithMessageAsArray:(NSArray *)message keepCallback:(BOOL)keepCallback;
- (void) errorWithMessageAsBool:(BOOL)message keepCallback:(BOOL)keepCallback;
- (void) errorWithMessageAsDictionary:(NSDictionary *)message keepCallback:(BOOL)keepCallback;
- (void) errorWithMessageAsDouble:(double)message keepCallback:(BOOL)keepCallback;
- (void) errorWithMessageAsInt:(int)message keepCallback:(BOOL)keepCallback;
- (void) errorWithMessageAsString:(NSString *)message keepCallback:(BOOL)keepCallback;

@end

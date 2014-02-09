#import "SVNHWebViewManager.h"
#import "SVNHPluginResult.h"

/*!
 * A Command is used to send the result of a Plugin execution back to the UIWebView that called it.
 */
@interface SVNHCommand : NSObject

/*!
 * The arguments passed to the Plugin by the UIWebView.
 */
@property (nonatomic, readonly) NSArray* arguments;

/*!
 * The WebViewManager for the UIWebView.
 */
@property (nonatomic, readonly) SVNHWebViewManager* webViewManager;

/*!
 * The UIWebView that executed this Command.
 */
@property (nonatomic, readonly) UIWebView* webView;

/*!
 * Create a new Command.
 * @param arguments the arguments passed to the plugin by the UIWebVeiw.
 * @param callbackId the value used to identify the callbacks for this Command in the UIWebView.
 * @param webViewManager the manager for this Command.
 * @param webView the UIWebView for this Command.
 */
- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
          webViewManager:(SVNHWebViewManager *)webViewManager
                 webView:(UIWebView *)webView;

/*!
 * Returns the argument at the given index.
 * @param index the index of the returned argument.
 * @return the argument at the given index, or nil if the given index is out of bounds.
 */
- (id) argumentAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index.
 * @param index the index of the returned argument.
 * @param defaultValue the value to return if there is no argument at the given index or the argument at the given index is null.
 * @return the argument at the given index, or defaultValue if there is no argument at the given index or the argument at the given index is null.
 */
- (id) argumentAtIndex:(NSUInteger)index
           withDefault:(id)defaultValue;

/*!
 * Returns the argument at the given index.
 * @param index the index of the returned argument.
 * @param defaultValue the value to return if there is no argument at the given index or the argument at the given index is null or the value at the given index is not an instance of the given class.
 * @param aClass if the value at the given index is not a subclass of this class, the defaultValue is returned.
 * @return the argument at the given index, or defaultValue if there is no argument at the given index or the argument at the given index is null or the value at the given index is not an instance of the given class.
 */
- (id) argumentAtIndex:(NSUInteger)index
           withDefault:(id)defaultValue
              andClass:(Class)aClass;

/*!
 * Sends the given result to the WebViewManager to pass on to the UIWebView.
 * @param result The result of the Command.
 */
- (void)sendPluginResult:(SVNHPluginResult *)result;

/*!
 * Calls the success callback in the WebView for this command (if any).
 * @param keepCallback true if additional responses will be sent back to the WebView for this Command, false otherwise.
 */
- (void) successAndKeepCallback:(BOOL)keepCallback;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a JSON array.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) successWithMessageAsArray:(NSArray *)message
                      keepCallback:(BOOL)keepCallback;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a boolean.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) successWithMessageAsBool:(BOOL)message
                     keepCallback:(BOOL)keepCallback;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a JSON object.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) successWithMessageAsDictionary:(NSDictionary *)message
                           keepCallback:(BOOL)keepCallback;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a double.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) successWithMessageAsDouble:(double)message
                       keepCallback:(BOOL)keepCallback;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back an int.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) successWithMessageAsInt:(int)message
                    keepCallback:(BOOL)keepCallback;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a string.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) successWithMessageAsString:(NSString *)message
                       keepCallback:(BOOL)keepCallback;

/*!
 * Calls the error callback in the UIWebView for this command (if any).
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) errorAndKeepCallback:(BOOL)keepCallback;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a JSON array.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) errorWithMessageAsArray:(NSArray *)message
                    keepCallback:(BOOL)keepCallback;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a boolean.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) errorWithMessageAsBool:(BOOL)message
                   keepCallback:(BOOL)keepCallback;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a JSON object.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) errorWithMessageAsDictionary:(NSDictionary *)message
                         keepCallback:(BOOL)keepCallback;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a double.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) errorWithMessageAsDouble:(double)message
                     keepCallback:(BOOL)keepCallback;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back an int.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) errorWithMessageAsInt:(int)message
                  keepCallback:(BOOL)keepCallback;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a string.
 * @param message the result of the Command, to send to the UIWebView.
 * @param keepCallback true if additional responses will be sent back to the UIWebView for this Command, false otherwise.
 */
- (void) errorWithMessageAsString:(NSString *)message
                     keepCallback:(BOOL)keepCallback;

@end

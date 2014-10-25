#import "SVNHWebViewManager.h"

/*!
 * A Command is used to send the result of a Plugin execution back to the UIWebView that called it.
 */
@interface SVNHCommand : NSObject

/*!
 * The WebViewManager for the UIWebView.
 */
@property (nonatomic, readonly) NSString* webViewManagerName;

/*!
 * The UIWebView that executed this Command.
 */
@property (nonatomic, readonly) UIWebView* webView;

/*!
 * The command's keep callback flag.
 * @deprecated
 * @param keepCallback true if the next call to success or error should keep this Command and not discard it, false otherwise.
 * @discussion You should only set this property when porting over plugins that use Cordova syntax. Use progress instead.
 */
@property (nonatomic) BOOL keepCallback __attribute__((deprecated));

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
 * Calls the success callback in the WebView for this command (if any), and discards the Command.
 */
- (void) success;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a JSON array, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithArray:(NSArray *)message;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a boolean, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithBool:(BOOL)message;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a JSON object, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithDictionary:(NSDictionary *)message;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a double, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithDouble:(double)message;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back an int, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithInt:(int)message;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a string, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithString:(NSString *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), and discards the Command.
 */
- (void) error;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a JSON array, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithArray:(NSArray *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a boolean, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithBool:(BOOL)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a JSON object, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithDictionary:(NSDictionary *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a double, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithDouble:(double)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back an int, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithInt:(int)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a string, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithString:(NSString *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any).
 */
- (void) progress;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a JSON array.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithArray:(NSArray *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a boolean.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithBool:(BOOL)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a JSON object.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithDictionary:(NSDictionary *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a double.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithDouble:(double)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back an int.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithInt:(int)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a string.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithString:(NSString *)message;

@end

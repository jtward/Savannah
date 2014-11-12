#import <UIKit/UIKit.h>

@class SVNHWebViewManager;

/*!
 * A Command is used to send the result of a Plugin execution back to the UIWebView that called it.
 */
@interface SVNHCommand : NSObject

/*!
 * The WebViewManager for the UIWebView.
 */
@property (nonatomic, readonly) NSString* webViewManagerName;

/*!
 * Create a new Command.
 * @param arguments the arguments passed to the plugin by the UIWebView.
 * @param callbackId the value used to identify the callbacks for this Command in the UIWebView.
 * @param webViewManager the manager for this Command.
 */
- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
          webViewManager:(SVNHWebViewManager *)webViewManager;

/*!
 * Returns the length of the arguments array for this Command.
 * @return the length of the arguments array for this Command
 */
- (long) argumentsCount;

/*!
 * Returns YES if the argument at the given index is an array. If index is beyond the end of the array, then this method returns NO.
 * @param index the index into the arguments array to check.
 * @return YES if the argument at the given index is an array, NO otherwise.
 */
- (BOOL) hasArrayAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is an array. If the argument at the given index is not an array, then this method returns nil.
 * @param index the index into the arguments array to check.
 * @return the argument at the given index if it is an array, nil otherwise.
 */
- (NSArray *) arrayAtIndex:(NSUInteger)index;

/*!
 * Returns YES if the argument at the given index is a bool. Note that the numbers 1 and 0 are indistinguishable from YES and NO, respectively, and so will this method will return YES if the argument is numeric and has the value 1 or 0. If index is beyond the end of the array, then this method returns NO.
 * @param index the index into the arguments array to check.
 * @return YES if the argument at the given index is a bool, NO otherwise.
 */
- (BOOL) hasBoolAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is a bool. If the argument at the given index is not an array, then this method returns NO. If the argument at the given index is not a bool, then this method returns nil. Note that the numbers 1 and 0 are indistinguishable from YES and NO, respectively, and so will this method will return YES if the argument is 1 and NO if the argument is 0.
 * @param index the index into the arguments array to check.
 * @return the argument at the given index if it is a bool, NO otherwise.
 */
- (BOOL) boolAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is a bool. If the argument at the given index is not a bool, then this method returns nil. Note that the numbers 1 and 0 are indistinguishable from YES and NO, respectively, and so will this method will return YES if the argument is 1 and NO if the argument is 0.
 * @param index the index into the arguments array to check.
 * @param defaultValue the value to return if the argument at the given index is not a bool.
 * @return the argument at the given index if it is a bool, defaultValue otherwise.
 */
- (BOOL) boolAtIndex:(NSUInteger)index
        defaultValue:(BOOL)defaultValue;

/*!
 * Returns YES if the argument at the given index is a dictionary. If index is beyond the end of the array, then this method returns NO.
 * @param index the index into the arguments array to check.
 * @return YES if the argument at the given index is a dictionary, NO otherwise.
 */
- (BOOL) hasDictionaryAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is a dictionary. If the argument at the given index is not a dictionary, then this method returns nil.
 * @param index the index into the arguments array to check.
 * @return the argument at the given index if it is a dictionary, nil otherwise.
 */
- (NSDictionary *) dictionaryAtIndex:(NSUInteger)index;

/*!
 * Returns YES if the argument at the given index is a double. If index is beyond the end of the array, then this method returns NO.
 * @param index the index into the arguments array to check.
 * @return YES if the argument at the given index is a double, NO otherwise.
 */
- (BOOL) hasDoubleAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is a double.
 * @param index the index into the arguments array to check.
 * @return the argument at the given index if it is a double, 0 otherwise.
 */
- (double) doubleAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is a double.
 * @param index the index into the arguments array to check.
 * @param defaultValue the value to return if the argument at the given index is not a double.
 * @return the argument at the given index if it is a double, defaultValue otherwise.
 */
- (double) doubleAtIndex:(NSUInteger)index
            defaultValue:(double)defaultValue;

/*!
 * Returns YES if the argument at the given index is an int. If index is beyond the end of the array, then this method returns NO.
 * @param index the index into the arguments array to check.
 * @return YES if the argument at the given index is an int, NO otherwise.
 */
- (BOOL) hasIntAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is an int.
 * @param index the index into the arguments array to check.
 * @return the argument at the given index if it is an int, 0 otherwise.
 */
- (int) intAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is an int.
 * @param index the index into the arguments array to check.
 * @param defaultValue the value to return if the argument at the given index is not an int.
 * @return the argument at the given index if it is an int, defaultValue otherwise.
 */
- (int) intAtIndex:(NSUInteger)index
      defaultValue:(int)defaultValue;

/*!
 * Returns YES if the argument at the given index is a string. If index is beyond the end of the array, then this method returns NO.
 * @param index the index into the arguments array to check.
 * @return YES if the argument at the given index is a string, NO otherwise.
 */
- (BOOL) hasStringAtIndex:(NSUInteger)index;

/*!
 * Returns the argument at the given index if it is a string.
 * @param index the index into the arguments array to check.
 * @return the argument at the given index if it is a string, nil otherwise.
 */
- (NSString *) stringAtIndex:(NSUInteger)index;

/*!
 * Returns YES if the argument at the given index is nil. If index beyond the end of the array, then this method returns YES.
 * @param index the index into the arguments array to check.
 * @return YES if the argument at the given index is nil.
 */
- (BOOL) hasNilAtIndex:(NSUInteger)index;

/*!
 * Calls the success callback in the WebView for this command (if any), and discards the Command.
 */
- (void) success;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back an array, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithArray:(NSArray *)message;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a bool, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) successWithBool:(BOOL)message;

/*!
 * Calls the success callback in the UIWebView for this command (if any), passing back a dictionary, and discards the Command.
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
 * Calls the error callback in the UIWebView for this command (if any), passing back an array, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithArray:(NSArray *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a bool, and discards the Command.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) errorWithBool:(BOOL)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a dictionary, and discards the Command.
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
 * Calls the error callback in the UIWebView for this command (if any), passing back an array.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithArray:(NSArray *)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a bool.
 * @param message the result of the Command, to send to the UIWebView.
 */
- (void) progressWithBool:(BOOL)message;

/*!
 * Calls the error callback in the UIWebView for this command (if any), passing back a dictionary.
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

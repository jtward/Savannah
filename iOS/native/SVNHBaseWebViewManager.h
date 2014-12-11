#import <Foundation/Foundation.h>
@protocol SVNHPlugin;

@interface SVNHBaseWebViewManager : NSObject {
    // allow _name to be overridden in subclasses
    @protected
    NSString *_name;
}

/*!
 * Returns the name given to this WebViewManager.
 * @return the name given to this WebViewManager.
 */
@property (nonatomic, readonly) NSString *name;

/*!
 * Returns the registered plugin with the given name, or nil if no plugin with the given name is registered.
 * @param pluginName the name of the registered plugin to return.
 */
- (id<SVNHPlugin>) getPluginByName:(NSString *)pluginName;

/*!
 * Executes the given script in the WebViewManager's WebView.
 * @param script the script to execute.
 * @return the result of the execution.
 */
- (void) executeJavaScript:(NSString *)script
         completionHandler:(void (^)(id, NSError *))completionHandler;

@end
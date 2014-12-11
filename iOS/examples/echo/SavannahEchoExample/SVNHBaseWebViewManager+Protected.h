#import "SVNHBaseWebViewManager.h"
#import "SVNHConfigProvider.h"

@class SVNHConfigProvider;

@interface SVNHBaseWebViewManager (Protected)

@property (nonatomic) NSMutableDictionary *pendingCommands;
@property (nonatomic) NSDictionary *plugins;
@property (nonatomic) NSString *settingsJSON;

/*!
 * Reset the state of this manager, and reinitialize with a new set of plugins and settings ready for a new page to load.
 * @param plugins The plugins to be used for the next page.
 * @param settings The settings to be used for the next page.
 */
- (void) resetWithPlugins:(NSArray *)plugins
                 settings:(NSDictionary *)settings;

/*!
 * Send a message to the webview to tell it that the manager is ready to receive commands.
 */
- (void) finishWebviewLoad;

/*!
 * Handle commands from the webview, calling plugin methods.
 * @param commands The commands to handle.
 */
- (void) handleCommands:(NSArray *)commands;


/*!
 * Sends the result of a Plugin execution to the UIWebView.
 * @param status the status to send.
 * @param message the message to send.
 * @param keepCallback true if the callback should be kept rather than discarded.
 * @param callbackId the id of the callback that should receive the result.
 */
- (void) sendPluginResponseWithStatus:(BOOL)status
                              message:(NSString *)message
                         keepCallback:(BOOL)keepCallback
                           callbackId:(NSString *)callbackId;

@end
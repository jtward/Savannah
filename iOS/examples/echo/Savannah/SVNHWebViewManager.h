#import "SVNHPluginResult.h"

@interface SVNHWebViewManager : NSObject <UIWebViewDelegate>

- (id) initWithName:(NSString *)name WebView:(UIWebView *)webView plugins:(NSArray *)plugins URL:(NSURL *)URL;
- (void) sendPluginResult:(SVNHPluginResult *)result withCallbackId:(NSString *)callbackId;

@property (nonatomic, readonly) NSString *name;

@end

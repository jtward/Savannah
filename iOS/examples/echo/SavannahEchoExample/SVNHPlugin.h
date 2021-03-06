#import <Foundation/Foundation.h>

@class SVNHCommand;

/*!
 * A Plugin is a class whose methods can be invoked from a UIWebView.
 */
@protocol SVNHPlugin <NSObject>

@required

/*!
 * Returns the name of the Plugin. This name is used in the WebView to identify the Plugin that should receive the command and should be a reversed FQDN.
 * @return the name of the Plugin.
 */
+ (NSString *) name;
+ (NSArray *) methods;

- (BOOL) execute:(NSString *)action
     withCommand:(SVNHCommand *)command;

@end

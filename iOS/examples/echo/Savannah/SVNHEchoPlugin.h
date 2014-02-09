#import <Foundation/Foundation.h>
#import "SVNHPlugin.h"
#import "SVNHCommand.h"

@interface SVNHEchoPlugin : NSObject <SVNHPlugin>

+ (NSString *) name;

- (void) echo:(UIWebView *)webView
         name:(NSString *)name
      command:(SVNHCommand *)command;
@end

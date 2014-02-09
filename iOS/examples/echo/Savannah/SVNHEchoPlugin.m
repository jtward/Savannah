#import "SVNHEchoPlugin.h"

@implementation SVNHEchoPlugin

+ (NSString *) name {
    return @"uk.co.tealspoon.savannah.echo";
};

- (void) echo:(UIWebView *)webView
         name:(NSString *)name
      command:(SVNHCommand *)command {
    
    NSString *message = [command argumentAtIndex:0];
    [command successWithMessageAsString:message keepCallback:NO];
}

@end

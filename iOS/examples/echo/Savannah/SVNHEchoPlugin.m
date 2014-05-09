#import "SVNHEchoPlugin.h"

@implementation SVNHEchoPlugin

+ (NSString *) name {
    return @"uk.co.tealspoon.savannah.echo";
};

- (void) echo:(SVNHCommand *)command {
    
    NSString *message = [command argumentAtIndex:0];
    [command successWithMessageAsString:message keepCallback:NO];
}

@end

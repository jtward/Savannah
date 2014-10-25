#import "SVNHEchoPlugin.h"

@implementation SVNHEchoPlugin

+ (NSString *) name {
    return @"uk.co.tealspoon.savannah.echo";
};

+ (NSArray *) methods {
    return @[@"echo"];
};

- (void) echo:(SVNHCommand *)command {
    
    NSString *message = [command argumentAtIndex:0];
    [command successWithString:message];
}

@end
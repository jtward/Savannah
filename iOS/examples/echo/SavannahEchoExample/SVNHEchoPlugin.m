#import "SVNHEchoPlugin.h"

@implementation SVNHEchoPlugin

+ (NSString *) name {
    return @"uk.co.tealspoon.savannah.echo";
};

+ (NSArray *) methods {
    return @[@"echo"];
};

- (BOOL) execute:(NSString *)action
     withCommand:(SVNHCommand *)command {
    if ([action isEqualToString:@"echo"]) {
        NSString *message = [command argumentAtIndex:0];
        [command successWithString:message];
        return YES;
    }
    
    return NO;
}

@end

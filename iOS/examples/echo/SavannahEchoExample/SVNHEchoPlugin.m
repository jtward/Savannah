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

        if ([command hasStringAtIndex:0]) {
            NSString *message = [command stringAtIndex:0];
            [command successWithString:message];
        }
        else {
            [command error];
        }

        return YES;
    }
    
    return NO;
}

@end

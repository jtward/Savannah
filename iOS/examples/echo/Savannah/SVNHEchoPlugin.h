#import <Foundation/Foundation.h>
#import "SVNHPlugin.h"
#import "SVNHCommand.h"

@interface SVNHEchoPlugin : NSObject <SVNHPlugin>

+ (NSString *) name;
+ (NSArray *) methods;

- (void) echo:(SVNHCommand *)command;
@end

#import "SVNHPlugin.h"
#import "SVNHCommand.h"

@interface SVNHEchoPlugin : NSObject <SVNHPlugin>

+ (NSString *) name;
+ (NSArray *) methods;

- (BOOL) execute:(NSString *)action
     withCommand:(SVNHCommand *)command;

@end

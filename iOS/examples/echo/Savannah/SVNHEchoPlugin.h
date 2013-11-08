//
//  SVNHLogPlugin.h
//  Savannah
//
//  Created by James Ward on 11/10/2013.
//
//

#import <Foundation/Foundation.h>
#import "SVNHPlugin.h"
#import "SVNHCommand.h"

@interface SVNHEchoPlugin : NSObject <SVNHPlugin>

+ (NSString *) name;

- (void)echo:(SVNHCommand *)command withArguments:(NSArray *)arguments;
@end

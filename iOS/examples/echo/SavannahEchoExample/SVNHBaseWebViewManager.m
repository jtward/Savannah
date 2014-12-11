#import <Foundation/Foundation.h>
#import "SVNHBaseWebViewManager.h"
#import "SVNHBaseWebViewManager+Protected.h"

@interface SVNHBaseWebViewManager()

@property (nonatomic) NSMutableDictionary *pendingCommands;
@property (nonatomic) NSDictionary *plugins;
@property (nonatomic) NSString *settingsJSON;

@end

@implementation SVNHBaseWebViewManager : NSObject

- (id <SVNHPlugin>) getPluginByName:(NSString *)pluginName {
    return [self.plugins objectForKey:pluginName];
}

- (NSString *) executeJavaScript:(NSString *)script {
    [self doesNotRecognizeSelector:_cmd];
    return nil;
}

- (void) executeJavaScript:(NSString *)script
         completionHandler:(void (^)(id, NSError *))completionHandler {
    [self doesNotRecognizeSelector:_cmd];
}

@end
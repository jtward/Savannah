#import "SVNHBaseWebViewManager+Protected.h"
#import "SVNHPlugin.h"
#import "SVNHCommand.h"

@implementation SVNHBaseWebViewManager (Protected)

@dynamic plugins;
@dynamic settingsJSON;
@dynamic pendingCommands;

- (void) resetWithPlugins:(NSArray *)plugins
                 settings:(NSDictionary *)settings {

    long pluginsCount = plugins == nil ? 0 : [plugins count];

    NSMutableDictionary *pluginsDictionary = [[NSMutableDictionary alloc] initWithCapacity:pluginsCount];

    for (id <SVNHPlugin> plugin in plugins) {

        [pluginsDictionary setObject:plugin
                              forKey:[[plugin class] name]];
    }

    self.plugins = [pluginsDictionary copy];

    self.pendingCommands = [NSMutableDictionary new];

    settings = settings ?: [NSDictionary new];

    NSData* settingsData = [NSJSONSerialization dataWithJSONObject:settings
                                                           options:0
                                                             error:nil];

    self.settingsJSON = [[NSString alloc] initWithData:settingsData
                                              encoding:NSUTF8StringEncoding];

    NSLog(@"Settings: %@", self.settingsJSON);
}

- (void) handleCommands:(NSArray *)commands {
    for(NSArray *cmd in commands) {

        NSString *pluginName = [cmd objectAtIndex:1];
        NSString *methodName = [cmd objectAtIndex:2];

        id <SVNHPlugin> plugin = [self.plugins objectForKey:pluginName];

        if (plugin == nil) {
            NSLog(@"Plugin %@ not found", pluginName);
        }
        else {
            NSString *callbackId = [cmd objectAtIndex:0];
            SVNHCommand *command = [[SVNHCommand alloc] initWithArguments:[cmd objectAtIndex:3]
                                                               callbackId:callbackId
                                                           webViewManager:self];

            if ([self.pendingCommands objectForKey:callbackId] == nil) {
                [self.pendingCommands setObject:command forKey:callbackId];
                [plugin execute:methodName withCommand:command];
            }
            else {
                NSLog(@"Command with callback ID %@ is already pending", callbackId);
            }
        }
    }
}

- (void) finishWebviewLoad {
    NSData *pluginNamesData = [NSJSONSerialization dataWithJSONObject:[self.plugins allKeys]
                                                              options:0
                                                                error:nil];

    NSString *pluginNamesJSON = [[NSString alloc] initWithData:pluginNamesData
                                                      encoding:NSUTF8StringEncoding];

    NSMutableArray *pluginMethods = [[NSMutableArray alloc] initWithCapacity:[self.plugins count]];
    for (id <SVNHPlugin> plugin in [self.plugins allValues]) {
        [pluginMethods addObject:[[plugin class] methods]];
    }

    NSData *pluginMethodsData = [NSJSONSerialization dataWithJSONObject:pluginMethods
                                                                options:0
                                                                  error:nil];

    NSString *pluginMethodsJSON = [[NSString alloc] initWithData:pluginMethodsData
                                                        encoding:NSUTF8StringEncoding];

    [self executeJavaScript:[NSString stringWithFormat:@"window.savannah._didFinishLoad(%@, %@, %@);",
                             self.settingsJSON,
                             pluginNamesJSON,
                             pluginMethodsJSON]
          completionHandler:nil];
}

- (void) sendPluginResponseWithStatus:(BOOL)status
                              message:(NSString *)message
                         keepCallback:(BOOL)keepCallback
                           callbackId:(NSString *)callbackId {

    if ([self.pendingCommands objectForKey:callbackId] == nil) {
        NSLog(@"Command with callback ID %@ is not pending", callbackId);
        return;
    }

    if (!keepCallback) {
        [self.pendingCommands removeObjectForKey:callbackId];
    }

    NSString *stringStatus = status ? @"true" : @"false";

    NSString *execString = [NSString stringWithFormat:@"window.savannah._callback('%@',%@,%@,%d);",
                            callbackId,
                            stringStatus,
                            message,
                            keepCallback];

    [self executeJavaScript:execString completionHandler:nil];
}

@end
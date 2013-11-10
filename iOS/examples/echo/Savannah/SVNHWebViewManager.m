/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

#import "SVNHWebViewManager.h"
#import "SVNHCommand.h"

@implementation SVNHWebViewManager

@synthesize isFirstRequest;
@synthesize webView;

- (id) initWithWebView:(UIWebView *)theWebView plugins:(NSArray *)plugins URL:(NSURL *)URL {
    self = [super init];
    
    self.webView = theWebView;
    [self.webView setDelegate:self];
    
    NSMutableDictionary *pluginsDictionary = [[NSMutableDictionary alloc] initWithCapacity:(plugins == nil ? 0 : [plugins count])];
    for (id <SVNHPlugin> plugin in plugins) {
        [pluginsDictionary setObject:plugin forKey:[[plugin class] name]];
    }

    self.plugins = pluginsDictionary;
    self.isFirstRequest = YES;
    self.isFirstLoad = YES;
    
    NSURLRequest* appReq = [NSURLRequest requestWithURL:URL];
    [self.webView loadRequest:appReq];
    
    return self;
}

// allow plugins to be registered to a webview after initializing the delegate
- (void) registerPlugin:(id <SVNHPlugin>)plugin {
    [self.plugins setObject:plugin forKey:[plugin name]];
}

- (BOOL)webView:(UIWebView *)theWebView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    if (self.isFirstRequest) {
        self.isFirstRequest = NO;
        return YES;
    }
    else if ([[request.URL path] isEqualToString:@"/!svnh_exec"]) {
        NSString *cmds = [theWebView stringByEvaluatingJavaScriptFromString:@"window.savannah.nativeFetchMessages()"];
        NSError *error;
        NSArray *cmdsArray = [NSJSONSerialization JSONObjectWithData:[cmds dataUsingEncoding:NSUTF8StringEncoding] options:0 error:&error];
        if (error != nil) {
            NSLog(@"Malformed JSON: %@", cmds);
        }
        else {
            for(NSArray *cmd in cmdsArray) {
                NSString *pluginName = [cmd objectAtIndex:1];
                NSString *methodName = [cmd objectAtIndex:2];
                id <SVNHPlugin> plugin = [self.plugins objectForKey:pluginName];
                if (plugin == nil) {
                    NSLog(@"Plugin %@ not found", pluginName);
                }
                else {
                    SEL execSelector = NSSelectorFromString([NSString stringWithFormat:@"%@:",methodName]);
                    if ([plugin respondsToSelector:execSelector]) {
                        // create command and send
                        SVNHCommand *command = [[SVNHCommand alloc] initWithArguments:[cmd objectAtIndex:3] callbackId:[cmd objectAtIndex:0] className:pluginName methodName:methodName webViewDelegate:self webView:theWebView];
                        [plugin performSelector:execSelector withObject:command];
                    }
                    else {
                        NSLog(@"Plugin %@ has no method %@", pluginName, methodName);
                    }
                }
            }
        }
        return NO;
    }
    else {
        return YES;
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)theWebView {
    if (self.isFirstLoad) {
        [theWebView stringByEvaluatingJavaScriptFromString:@"window.savannah.didFinishLoad()"];
        self.isFirstLoad = NO;
    }
}

- (void) sendPluginResult:(SVNHPluginResult *)result toWebView:(UIWebView *)theWebView withCallbackId:(NSString *)callbackId {
    NSString *arguments = [result argumentsAsJSON];
    NSString *status = result.status ? @"true" : @"false";
    BOOL keepCallback = result.keepCallback;
    NSString *execString = [NSString stringWithFormat:@"window.savannah.nativeCallback('%@',%@,%@,%d);", callbackId, status, arguments, keepCallback];
    [theWebView stringByEvaluatingJavaScriptFromString:execString];
}

@end

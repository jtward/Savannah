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

#import "SVNHCommand.h"
#import "SVNHWebViewManager.h"

@implementation SVNHCommand

- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
               className:(NSString *)className
              methodName:(NSString *)methodName
         webViewManager:(SVNHWebViewManager *)webViewManager
                 webView:(UIWebView *)webView {
    self = [super init];
    if (self != nil) {
        _arguments = arguments;
        _callbackId = callbackId;
        _className = className;
        _methodName = methodName;
        _webViewManager = webViewManager;
        _webView = webView;
    }
    return self;
}

- (id)argumentAtIndex:(NSUInteger)index
{
    return [self argumentAtIndex:index withDefault:nil];
}

- (id)argumentAtIndex:(NSUInteger)index withDefault:(id)defaultValue
{
    return [self argumentAtIndex:index withDefault:defaultValue andClass:nil];
}

- (id)argumentAtIndex:(NSUInteger)index withDefault:(id)defaultValue andClass:(Class)aClass
{
    if (index >= [_arguments count]) {
        return defaultValue;
    }
    id ret = [_arguments objectAtIndex:index];
    if (ret == [NSNull null]) {
        ret = defaultValue;
    }
    if ((aClass != nil) && ![ret isKindOfClass:aClass]) {
        ret = defaultValue;
    }
    return ret;
}

- (void)sendPluginResult:(SVNHPluginResult *)result {
    [self.webViewManager sendPluginResult:result toWebView:self.webView withCallbackId:self.callbackId];
}

- (void) successAndKeepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES keepCallback:keepCallback message:nil]];
}

- (void) successWithMessageAsArray:(NSArray *)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES keepCallback:keepCallback message:message]];
}

- (void) successWithMessageAsBool:(BOOL)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES keepCallback:keepCallback message:[NSNumber numberWithBool:message]]];
}

- (void) successWithMessageAsDictionary:(NSDictionary *)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES keepCallback:keepCallback message:message]];
}

- (void) successWithMessageAsDouble:(double)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES keepCallback:keepCallback message:[NSNumber numberWithInt:message]]];
}

- (void) successWithMessageAsInt:(int)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES keepCallback:keepCallback message:[NSNumber numberWithDouble:message]]];
}

- (void) successWithMessageAsString:(NSString *)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:YES keepCallback:keepCallback message:message]];
}

- (void) errorAndKeepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO keepCallback:keepCallback message:nil]];
}

- (void) errorWithMessageAsArray:(NSArray *)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO keepCallback:keepCallback message:message]];
}

- (void) errorWithMessageAsBool:(BOOL)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO keepCallback:keepCallback message:[NSNumber numberWithBool:message]]];
}

- (void) errorWithMessageAsDictionary:(NSDictionary *)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO keepCallback:keepCallback message:message]];
}

- (void) errorWithMessageAsDouble:(double)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO keepCallback:keepCallback message:[NSNumber numberWithInt:message]]];
}

- (void) errorWithMessageAsInt:(int)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO keepCallback:keepCallback message:[NSNumber numberWithDouble:message]]];
}

- (void) errorWithMessageAsString:(NSString *)message keepCallback:(BOOL)keepCallback {
    [self sendPluginResult:[[SVNHPluginResult alloc] initWithSuccess:NO keepCallback:keepCallback message:message]];
}

@end

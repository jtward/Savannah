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
#import "SVNHWebViewDelegate.h"
#import "NSData+Base64.h"

@implementation SVNHCommand

- (id) initWithArguments:(NSArray *)arguments
              callbackId:(NSString *)callbackId
               className:(NSString *)className
              methodName:(NSString *)methodName
         webViewDelegate:(SVNHWebViewDelegate *)webViewDelegate
                 webView:(UIWebView *)webView {
    self = [super init];
    if (self != nil) {
        _arguments = arguments;
        _callbackId = callbackId;
        _className = className;
        _methodName = methodName;
        _webViewDelegate = webViewDelegate;
        _webView = webView;
    }
    [self massageArguments];
    return self;
}

- (void)massageArguments
{
    NSMutableArray* newArgs = nil;
    
    for (NSUInteger i = 0, count = [_arguments count]; i < count; ++i) {
        id arg = [_arguments objectAtIndex:i];
        if (![arg isKindOfClass:[NSDictionary class]]) {
            continue;
        }
        NSDictionary* dict = arg;
        NSString* type = [dict objectForKey:@"SVNHType"];
        if (!type || ![type isEqualToString:@"ArrayBuffer"]) {
            continue;
        }
        NSString* data = [dict objectForKey:@"data"];
        if (!data) {
            continue;
        }
        if (newArgs == nil) {
            newArgs = [NSMutableArray arrayWithArray:_arguments];
            _arguments = newArgs;
        }
        [newArgs replaceObjectAtIndex:i withObject:[NSData dataFromBase64String:data]];
    }
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
    // TODO get arguments from result
    [self.webViewDelegate sendPluginResult:result toWebView:self.webView withCallbackId:self.callbackId];
}

@end

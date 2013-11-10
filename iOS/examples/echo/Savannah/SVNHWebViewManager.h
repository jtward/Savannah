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


#import <Foundation/Foundation.h>
#import "SVNHPlugin.h"
#import "SVNHPluginResult.h"

@class SVNHCommand;
@interface SVNHWebViewManager : NSObject <UIWebViewDelegate>

- (id) initWithWebView:(UIWebView *)webView plugins:(NSArray *)plugins URL:(NSURL *)URL;
- (void) sendPluginResult:(SVNHPluginResult *)result toWebView:(UIWebView *)webView withCallbackId:(NSString *)callbackId;

@property (strong, nonatomic) NSMutableDictionary *plugins;
@property (strong, nonatomic) UIWebView *webView;
@property (nonatomic) BOOL isFirstRequest;
@property (nonatomic) BOOL isFirstLoad;

@property(strong, nonatomic) id<UIWebViewDelegate> delegate;

@end

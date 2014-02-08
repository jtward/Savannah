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

#import "SVNHPluginResult.h"

@interface SVNHPluginResult ()

@property (nonatomic) BOOL status;
@property (nonatomic) BOOL keepCallback;
@property (nonatomic) NSString *message;

@end

@implementation SVNHPluginResult
@synthesize status, message, keepCallback;

static NSArray* CommandStatusMsgs;

- (SVNHPluginResult*)initWithSuccess:(BOOL)success keepCallback:(BOOL)shouldKeepCallback message:(id)messageObject {
    self = [super init];
    self.status = success;
    self.keepCallback = shouldKeepCallback;
    self.message = [self messageAsJSON:messageObject];
    return self;
}

- (NSString*)messageAsJSON:(id)messageObject {
    
    if (messageObject == nil) {
        messageObject = [NSNull null];
    }
    
    NSData* messageData = [NSJSONSerialization dataWithJSONObject:[NSArray arrayWithObject:messageObject]
                                                          options:0
                                                            error:nil];
    
    NSString *messageJSON = [[NSString alloc] initWithData:messageData encoding:NSUTF8StringEncoding];
    
    return [messageJSON substringWithRange:NSMakeRange(1, [messageJSON length] - 2)];
}

@end
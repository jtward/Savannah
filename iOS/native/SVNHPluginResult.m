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

- (SVNHPluginResult*)initWithSuccess:(BOOL)success keepCallback:(BOOL)keepCallback message:(id)message;

@end

@implementation SVNHPluginResult
@synthesize status, message, keepCallback;

static NSArray* CommandStatusMsgs;

- (SVNHPluginResult*)initWithSuccess:(BOOL)success keepCallback:(BOOL)shouldKeepCallback message:(id)theMessage {
    self = [super init];
    self.status = success;
    self.keepCallback = shouldKeepCallback;
    self.message = theMessage;
    return self;
}

- (NSString*)argumentsAsJSON
{
    id arguments = (self.message == nil ? [NSNull null] : self.message);
    
    NSData* argumentsJSON = [NSJSONSerialization dataWithJSONObject:[NSArray arrayWithObject:arguments]
                                                            options:0
                                                              error:nil];
    
    NSString *argumentsString = [[NSString alloc] initWithData:argumentsJSON encoding:NSUTF8StringEncoding];
    
    NSString *returnedArguments = [argumentsString substringWithRange:NSMakeRange(1, [argumentsString length] - 2)];
    
    return returnedArguments;
}

@end
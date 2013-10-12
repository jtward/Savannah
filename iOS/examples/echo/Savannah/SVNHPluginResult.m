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
#import "SVNHJSON.h"
#import "NSData+Base64.h"

@interface SVNHPluginResult ()

- (SVNHPluginResult*)initWithStatus:(SVNHCommandStatus)statusOrdinal message:(id)theMessage;

@end

@implementation SVNHPluginResult
@synthesize status, message, keepCallback;

static NSArray* CommandStatusMsgs;

id messageFromArrayBuffer(NSData* data)
{
    return @{
             @"SVNHType" : @"ArrayBuffer",
             @"data" :[data base64EncodedString]
             };
}

id massageMessage(id message)
{
    if ([message isKindOfClass:[NSData class]]) {
        return messageFromArrayBuffer(message);
    }
    return message;
}

id messageFromMultipart(NSArray* theMessages)
{
    NSMutableArray* messages = [NSMutableArray arrayWithArray:theMessages];
    
    for (NSUInteger i = 0; i < messages.count; ++i) {
        [messages replaceObjectAtIndex:i withObject:massageMessage([messages objectAtIndex:i])];
    }
    
    return @{
             @"CDVType" : @"MultiPart",
             @"messages" : messages
             };
}

+ (void)initialize
{
    CommandStatusMsgs = [[NSArray alloc] initWithObjects:@"No result",
                                            @"OK",
                                            @"Class not found",
                                            @"Illegal access",
                                            @"Instantiation error",
                                            @"Malformed url",
                                            @"IO error",
                                            @"Invalid action",
                                            @"JSON error",
                                            @"Error",
                                            nil];
}


- (SVNHPluginResult*)init
{
    return [self initWithStatus:SVNHCommandStatus_NO_RESULT message:nil];
}

- (SVNHPluginResult*)initWithStatus:(SVNHCommandStatus)statusOrdinal message:(id)theMessage
{
    self = [super init];
    if (self) {
        status = [NSNumber numberWithInt:statusOrdinal];
        message = theMessage;
        keepCallback = [NSNumber numberWithBool:NO];
    }
    return self;
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal
{
    return [[self alloc] initWithStatus:statusOrdinal message:[CommandStatusMsgs objectAtIndex:statusOrdinal]];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsString:(NSString*)theMessage
{
    return [[self alloc] initWithStatus:statusOrdinal message:theMessage];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsArray:(NSArray*)theMessage
{
    return [[self alloc] initWithStatus:statusOrdinal message:theMessage];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsInt:(int)theMessage
{
    return [[self alloc] initWithStatus:statusOrdinal message:[NSNumber numberWithInt:theMessage]];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsDouble:(double)theMessage
{
    return [[self alloc] initWithStatus:statusOrdinal message:[NSNumber numberWithDouble:theMessage]];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsBool:(BOOL)theMessage
{
    return [[self alloc] initWithStatus:statusOrdinal message:[NSNumber numberWithBool:theMessage]];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsDictionary:(NSDictionary*)theMessage
{
    return [[self alloc] initWithStatus:statusOrdinal message:theMessage];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsArrayBuffer:(NSData*)theMessage
{
    return [[self alloc] initWithStatus:statusOrdinal message:messageFromArrayBuffer(theMessage)];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsMultipart:(NSArray*)theMessages
{
    return [[self alloc] initWithStatus:statusOrdinal message:messageFromMultipart(theMessages)];
}

+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageToErrorObject:(int)errorCode
{
    NSDictionary* errDict = @{@"code" :[NSNumber numberWithInt:errorCode]};
    
    return [[self alloc] initWithStatus:statusOrdinal message:errDict];
}

- (void)setKeepCallbackAsBool:(BOOL)bKeepCallback
{
    [self setKeepCallback:[NSNumber numberWithBool:bKeepCallback]];
}

- (NSString*)argumentsAsJSON
{
    id arguments = (self.message == nil ? [NSNull null] : self.message);
    NSArray* argumentsWrappedInArray = [NSArray arrayWithObject:arguments];
    
    NSString* argumentsJSON = [argumentsWrappedInArray JSONString];
    
    argumentsJSON = [argumentsJSON substringWithRange:NSMakeRange(1, [argumentsJSON length] - 2)];
    
    return argumentsJSON;
}

@end
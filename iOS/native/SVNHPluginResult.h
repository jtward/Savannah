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

typedef enum {
    SVNHCommandStatus_NO_RESULT = 0,
    SVNHCommandStatus_OK,
    SVNHCommandStatus_CLASS_NOT_FOUND_EXCEPTION,
    SVNHCommandStatus_ILLEGAL_ACCESS_EXCEPTION,
    SVNHCommandStatus_INSTANTIATION_EXCEPTION,
    SVNHCommandStatus_MALFORMED_URL_EXCEPTION,
    SVNHCommandStatus_IO_EXCEPTION,
    SVNHCommandStatus_INVALID_ACTION,
    SVNHCommandStatus_JSON_EXCEPTION,
    SVNHCommandStatus_ERROR
} SVNHCommandStatus;

@interface SVNHPluginResult : NSObject

@property (nonatomic, strong, readonly) NSNumber* status;
@property (nonatomic, strong, readonly) id message;
@property (nonatomic, strong)           NSNumber* keepCallback;

- (SVNHPluginResult*)init;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsString:(NSString*)theMessage;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsArray:(NSArray*)theMessage;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsInt:(int)theMessage;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsDouble:(double)theMessage;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsBool:(BOOL)theMessage;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsDictionary:(NSDictionary*)theMessage;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsArrayBuffer:(NSData*)theMessage;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageAsMultipart:(NSArray*)theMessages;
+ (SVNHPluginResult*)resultWithStatus:(SVNHCommandStatus)statusOrdinal messageToErrorObject:(int)errorCode;

- (void)setKeepCallbackAsBool:(BOOL)bKeepCallback;

- (NSString*)argumentsAsJSON;

@end

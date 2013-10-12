/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

(function(window) {
    "use strict";
    var version = "0.0.1dev";

    // a container for all the unresolved callbacks
    var callbacks = {};

    // Contains pending JS->Native messages.
    var commandQueue = [];

    var massageArgsJsToNative = (function() {
        var base64FromArrayBuffer = (function() {
            // using the safari version from http://jsperf.com/b64tests/6
            var b64table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
            var b64product = {};
            var b64productstr = '';
            for (var i = 0; i < 64; i++) {
                for (var j = 0; j < 64; j++) {
                    b64product[i * 64 + j] = b64table[i] + b64table[j];
                    b64productstr += b64table[i] + b64table[j];
                }
            }

            return function(rawData) {
                /* jshint bitwise: false */
                var numBytes = rawData.byteLength;
                var url = "";
                var segment;
                for (var i = 0; i < numBytes - 2; i += 3) {
                    segment = (rawData[i] << 16) + (rawData[i + 1] << 8) + rawData[i + 2];
                    var i1 = segment >> 12,
                        i2 = segment & 0xfff;
                    // concat is faster on desktop safari
                    //        url += b64productstr[i1] + b64productstr[i2];
                    url.concat(b64productstr[i1]);
                    url.concat(b64productstr[i2]);
                }
                if (numBytes - i == 2) {
                    segment = (rawData[i] << 16) + (rawData[i + 1] << 8);
                    url += b64table[segment >> 18] + b64table[(segment & 0x3ffff) >> 12] + b64table[(segment & 0xfff) >> 6] + '=';
                } else if (numBytes - i == 1) {
                    segment = (rawData[i] << 16);
                    url += b64table[segment >> 18] + b64table[(segment & 0x3ffff) >> 12] + '==';
                }
            };
        }());

        return function(args) {
            if (!args || args instanceof Array) {
                return args;
            }
            var ret = [];

            args.forEach(function(arg) {
                if (arg instanceof ArrayBuffer) {
                    ret.push({
                        'SVNHType': 'ArrayBuffer',
                        'data': base64FromArrayBuffer(arg)
                    });
                } else {
                    ret.push(arg);
                }
            });
            return ret;
        };
    }());

    var massageMessageNativeToJs = (function() {
        var stringToArrayBuffer = function(str) {
            var ret = new Uint8Array(str.length);
            for (var i = 0; i < str.length; i++) {
                ret[i] = str.charCodeAt(i);
            }
            return ret.buffer;
        };

        var base64ToArrayBuffer = function(b64) {
            return stringToArrayBuffer(atob(b64));
        };

        return function(message) {
            return message.SVNHType === "ArrayBuffer" ?
                base64ToArrayBuffer(message.data) :
                message;
        };
    }());

    var exec = (function() {

        // a callback id that's randomized to minimize the possibility of clashes
        // after refreshes and navigations
        var callbackId = Math.floor(Math.random() * 2000000000);

        return function() {
            var successCallback = arguments[0];
            var failCallback = arguments[1];
            var service = arguments[2];
            var action = arguments[3];
            var actionArgs = massageArgsJsToNative(arguments[4]);

            var command = [callbackId, service, action, actionArgs];

            // remember the callbacks if given
            if (successCallback || failCallback) {
                callbacks[callbackId] = {
                    "success": successCallback,
                    "fail": failCallback
                };
                callbackId += 1;
            }

            commandQueue.push(JSON.stringify(command));

            if (commandQueue.length == 1) {
                window.location = "/!svnh_exec?";
            }
        };
    }());

    var nativeFetchMessages = function() {
        // Each entry in commandQueue is a JSON string already.
        if (!commandQueue.length) {
            return '';
        }
        var json = '[' + commandQueue.join(',') + ']';
        commandQueue.length = 0;
        return json;
    };

    var convertMessageToArgsNativeToJs = function(message) {
        var args = [];
        if (!message || !message.hasOwnProperty('CDVType')) {
            args.push(message);
        } else if (message.CDVType == 'MultiPart') {
            message.messages.forEach(function(e) {
                args.push(massageMessageNativeToJs(e));
            });
        } else {
            args.push(massageMessageNativeToJs(message));
        }
        return args;
    };

    var callbackFromNative = function(callbackId, success, status, args, keepCallback) {
        var callback = callbacks[callbackId];
        if (callback) {
            if (success && status === 1 && callback.success) {
                callback.success.apply(null, args);
            } else if (!success && callback.fail) {
                callback.fail.apply(null, args);
            }

            // Clear callback if not expecting any more results
            if (!keepCallback) {
                delete callbacks[callbackId];
            }
        } else {
            console.log("no such callback " + callbackId);
        }
    };

    var nativeCallback = function(callbackId, status, message, keepCallback) {
        var success = status === 0 || status === 1;
        var args = convertMessageToArgsNativeToJs(message);
        callbackFromNative(callbackId, success, status, args, keepCallback);
    };

    var onReady = (function() {
        var isLoadFinished = false;
        var callbacks = [];

        var onReady = function(callback) {
            if (typeof callback === "function") {
                if (isLoadFinished) {
                    callback();
                } else {
                    callbacks.push(callback);
                }
            }
        };

        var didFinishLoad = function() {
            var i;
            var localCallbacks = callbacks.slice(0);
            callbacks = [];
            isLoadFinished = true;
            for (i = 0; i < localCallbacks.length; i += 1) {
                try {
                    localCallbacks[i]();
                } finally {}
            }
        };
        return {
            onReady: onReady,
            didFinishLoad: didFinishLoad
        };
    }());

    window.savannah = {
        version: version,
        exec: exec,
        nativeFetchMessages: nativeFetchMessages,
        nativeCallback: nativeCallback,
        onReady: onReady.onReady,
        didFinishLoad: onReady.didFinishLoad
    };

}(window));
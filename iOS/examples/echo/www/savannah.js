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

    var exec = (function() {

        // a callback id that's randomized to minimize the possibility of clashes
        // after refreshes and navigations
        var callbackId = Math.floor(Math.random() * 2000000000);

        return function() {
            var successCallback = arguments[0];
            var failCallback = arguments[1];
            var service = arguments[2];
            var action = arguments[3];
            var actionArgs = arguments[4];

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

    var callbackFromNative = function(callbackId, success, args, keepCallback) {
        var callback = callbacks[callbackId];
        if (callback) {
            alert("the callback exists");
            if (success && callback.success) {
                alert("it has a success method!");
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

    var nativeCallback = function(callbackId, success, message, keepCallback) {
        callbackFromNative(callbackId, success, [message], keepCallback);
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
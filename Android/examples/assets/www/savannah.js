(function(window) {
    "use strict";
    var version = "0.4.0";

    // a container for all the unresolved callbacks
    var callbacks = {};

    // Contains pending JS->Native messages.
    var commandQueue = [];

    // keeps track of whether load is finished
    var isLoadFinished = false;

    var notifyNative = (function() {
        if (window.savannahJSI) {
            return function() {
                window.savannahJSI.exec(JSON.stringify(commandQueue));
                commandQueue.length = 0;
            };
        }
        else {
            return function() {
                window.location = "/!svnh_exec?";
            };
        }
    }());

    var exec = (function() {

        // a callback id that's randomized to minimize the possibility of clashes
        // after refreshes and navigations
        var callbackId = Math.floor(Math.random() * 2000000000);

        return function(successCallback, failCallback, service, action, actionArgs) {

            var command = [callbackId, service, action, actionArgs];

            // remember the callbacks if given
            if (successCallback || failCallback) {
                callbacks[callbackId] = {
                    "success": successCallback,
                    "fail": failCallback
                };
            }

            callbackId += 1;

            commandQueue.push(command);

            if (isLoadFinished) {
                notifyNative();
            }
        };
    }());

    var nativeFetchMessages = function() {
        // Each entry in commandQueue is a JSON string already.
        var json = JSON.stringify(commandQueue);
        commandQueue.length = 0;
        return json;
    };

    var callbackFromNative = function(callbackId, success, args, keepCallback) {
        var callback = callbacks[callbackId];
        if (callback) {
            if (success && callback.success) {
                callback.success.apply(null, args);
            } else if (!success && callback.fail) {
                callback.fail.apply(null, args);
            }

            // Clear callback if not expecting any more results
            if (!keepCallback) {
                delete callbacks[callbackId];
            }
        }
    };

    var nativeCallback = function(callbackId, success, message, keepCallback) {
        callbackFromNative(callbackId, success, [message], keepCallback);
    };

    var didFinishLoad = function(settings) {
        if (!isLoadFinished) {
            isLoadFinished = true;
            window.savannah.settings = settings;
            if (typeof window.savannah.onDeviceReady === "function") {
                window.savannah.onDeviceReady();
            }
            if (commandQueue.length > 0) {
                notifyNative();
            }
        }
    };

    window.savannah = {
        version: version,
        exec: exec,
        nativeFetchMessages: nativeFetchMessages,
        nativeCallback: nativeCallback,
        didFinishLoad: didFinishLoad
    };

}(window));
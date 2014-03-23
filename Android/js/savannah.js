(function(window) {
    "use strict";
    var version = "0.2.0";

    // a container for all the unresolved callbacks
    var callbacks = {};

    // Contains pending JS->Native messages.
    var commandQueue = [];

    // keeps track of whether load is finished
    var isLoadFinished = false;

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
            }

            callbackId += 1;

            if (!isLoadFinished) {
                commandQueue.push(command);
            }

            else {
                window.savannahJSI.exec(JSON.stringify([command]));
            }
        };
    }());

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

    var didFinishLoad = function() {
        if (!isLoadFinished) {
            isLoadFinished = true;
            if (commandQueue.length > 0) {
                window.savannahJSI.exec(JSON.stringify(commandQueue));
            }
        }
    }

    window.savannah = {
        version: version,
        exec: exec,
        nativeCallback: nativeCallback,
        didFinishLoad: didFinishLoad
    };

}(window));
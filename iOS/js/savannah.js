(function(window) {
    "use strict";
    var version = "0.1.0";

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

            commandQueue.push(JSON.stringify(command));

            if (isLoadFinished) {
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
                window.location = "/!svnh_exec?";
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
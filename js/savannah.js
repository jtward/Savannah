(function(window) {
    "use strict";
    var version = "0.6.0";

    // a container for all the unresolved callbacks
    var callbacks = {};

    // a container for promises
    var promises = {};

    // a container for progress callbacks
    var progressCallbacks = {};

    // a list of pending JS->Native messages.
    var commandQueue = [];

    // keeps track of whether load is finished
    var isLoadFinished = false;

    // notify the native app that there are commands waiting
    // send the command data if possible to avoid a round trip
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

    // IIFE; returns a function which is the entry point for all plugin execution
    var exec = (function() {

        // keep a record of the given callback for progress for the given callback ID
        var promiseProgress = function(callbackId, callback) {
            var callbacks = progressCallbacks[callbackId];
            if (!callbacks) {
                callbacks = (progressCallbacks[callbackId] = []);
            }
            callbacks.push(callback);
        };

        // a callback id that's randomized to minimize the possibility of clashes
        // after refreshes and navigations
        var callbackId = Math.floor(Math.random() * 2000000000);

        // the real exec
        return function(successCallback, failCallback, service, action, actionArgs) {
            var Promise = window.Promise;
            var tmpService;

            // exec can be called with or without leading success/fail params.
            // if the first param is a string, there were no success/fail params.
            if (typeof successCallback === "string") {
                tmpService = service;
                service = successCallback;
                action = failCallback;
                actionArgs = tmpService;
            }
            else if (successCallback || failCallback) {
                // if there were success/fail params, keep a record of them
                callbacks[callbackId] = {
                    "success": successCallback,
                    "fail": failCallback
                };
            }

            var command = [callbackId, service, action, actionArgs];

            callbackId += 1;

            commandQueue.push(command);

            if (isLoadFinished) {
                notifyNative();
            }

            if (Promise) {
                var promise = new Promise(function(resolve, reject) {
                    promises[command[0]] = {
                        resolve: resolve,
                        reject: reject
                    };
                });

                promise.progress = function(callback) {
                    promiseProgress(command[0], callback);
                    return this;
                };
                
                return promise;
            }
        };
    }());

    // let the native app pull commands
    var _fetchMessages = function() {
        // Each entry in commandQueue is a JSON string already.
        var json = JSON.stringify(commandQueue);
        commandQueue.length = 0;
        return json;
    };

    // call all progress listeners for the callback with the given arguments
    var notifyProgress = function(callbackId, args) {
        var listeners = progressCallbacks[callbackId];
        for (var i = 0; i < (listeners && listeners.length); i += 1) {
            listeners[i](args);
        }
    };

    // called when a response (success, fail or progress) is returned from the native app
    var _callback = function(callbackId, success, args, keepCallback) {
        var callback = callbacks[callbackId];
        var promise = promises[callbackId];

        if (callback) {
            if (success && callback.success) {
                callback.success.apply(null, [args]);
            } else if (!success && callback.fail) {
                callback.fail.apply(null, [args]);
            }

            // Clear callback if not expecting any more results
            if (!keepCallback) {
                delete callbacks[callbackId];
            }
        }
        
        if (promise) {
            if (keepCallback) {
                notifyProgress(callbackId, args);
            }
            else {
                (success ? promise.resolve : promise.reject)(args);
                delete promises[callbackId];
            }
        }
    };

    // called by the native app to register a plugin
    var _registerPlugin = function(pluginName) {
        window.savannah.plugins[pluginName] = function() {
            var args = Array.prototype.slice.call(arguments, 0);
            args.unshift(pluginName);
            return window.savannah.exec.apply(window.savannah, args);
        };
    };

    // called by the native app to unregister a plugin
    var _unregisterPlugin = function(pluginName) {
        delete window.savannah.plugins[pluginName];
    };

    // called by the native app to unregister all plugins
    var _clearPlugins = function() {
        window.savannah.plugins = {};
    };

    // called by the native app when the page load is complete, sending app-specific settings 
    var _didFinishLoad = function(settings, plugins) {
        var pluginName;

        if (!isLoadFinished) {
            isLoadFinished = true;
            window.savannah.settings = settings;
            for (var i = 0; i < plugins.length; i += 1) {
                _registerPlugin(plugins[i]);
            }
            if (typeof window.savannah.onDeviceReady === "function") {
                window.savannah.onDeviceReady();
            }
            if (commandQueue.length > 0) {
                notifyNative();
            }
        }
    };

    // exposed functions
    window.savannah = {
        _fetchMessages: _fetchMessages,
        _callback: _callback,
        _registerPlugin: _registerPlugin,
        _unregisterPlugin: _unregisterPlugin,
        _clearPlugins: _clearPlugins,
        _didFinishLoad: _didFinishLoad,
        exec: exec,
        plugins: {},
        version: version
    };

}(window));

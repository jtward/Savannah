(function(window) {
    "use strict";
    var version = "0.8.0";

    // a container for all the unresolved callbacks
    var callbacks = {};

    // a container for promises
    var promises = {};

    // a container for progress callbacks
    var progressCallbacks = {};

    // a list of pending JS->Native messages.
    var commandQueue = [];

    // a container for plugins
    var plugins = {};

    // a hash of fully qualified plugin names to their aliases
    var aliases = {};

    // keeps track of whether load is finished
    var isLoadFinished = false;

    // calls func on the leading edge and, if called again,
    // the trailing edge, of the debounce window.
    var debounce = function(func, wait) {
        var timeout;
        var didCallAgain;

        var later = function() {
            if (didCallAgain) {
                func();
            }
            timeout = null;
            didCallAgain = false;
        };

        return function() {
            if (!timeout) {
                timeout = setTimeout(later, wait);
                setTimeout(func, 0);
            }
            else {
                didCallAgain = true;
            }
        };
    };

    // notify the native app that there are commands waiting
    // send the command data if possible to avoid a round trip
    var notifyNative = debounce((function() {
        if (window.savannahJSI) {
            // Android
            return function() {
                if (commandQueue.length) {
                    window.savannahJSI.exec(JSON.stringify(commandQueue));
                    commandQueue.length = 0;
                }
            };
        }
        else {
            // iOS
            return function() {
                if (commandQueue.length) {
                    window.location = "/!svnh_exec?";
                }
            };
        }
    }()), 20);

    // IIFE; returns a function which is the entry point for all plugin execution
    var exec = (function() {

        // a callback id that's randomized to minimize the possibility of clashes
        // after refreshes and navigations
        var callbackId = Math.floor(Math.random() * 2000000000);

        // keep a record of the given callback for progress for the given callback ID
        var promiseProgress = function(callbackId, callback) {
            var callbacks = progressCallbacks[callbackId];
            if (!callbacks) {
                callbacks = (progressCallbacks[callbackId] = []);
            }
            callbacks.push(callback);
        };

        // the real exec
        return function(successCallback, failCallback, service, action, actionArgs) {
            var tmpService;
            var command;
            var promise;

            if (!isLoadFinished) {
                throw "Unable to execute plugin before Savannah is ready.";
            }

            // exec can be called with or without leading success/fail params.
            // if the first param is a string, there were no success/fail params.
            if (typeof successCallback === "string") {
                tmpService = service;
                service = successCallback;
                action = failCallback;
                actionArgs = tmpService;

                promise = new window.Promise(function(resolve, reject) {
                    promises[callbackId] = {
                        resolve: resolve,
                        reject: reject
                    };
                });

                promise.progress = function(callback) {
                    promiseProgress(callbackId, callback);
                    return this;
                };
            }
            else if (successCallback || failCallback) {
                // if there were success/fail params, keep a record of them
                callbacks[callbackId] = {
                    "success": successCallback,
                    "fail": failCallback
                };
            }

            command = [callbackId, service, action, actionArgs];

            callbackId += 1;

            commandQueue.push(command);

            notifyNative();

            return promise;
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
        var i;
        for (i = 0; i < (listeners && listeners.length); i += 1) {
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
            }
            else if (!success && callback.fail) {
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

    var pluginMethod = function(pluginName, methodName) {
        return function() {
            var args = [Array.prototype.slice.call(arguments, 0)];
            args.unshift(methodName);
            args.unshift(pluginName);
            return exec.apply(null, args);
        };
    };

    // called by the native app to register a plugin
    var _registerPlugin = function(pluginName, methods) {
        var plugin = {};
        var methodName;
        
        for (var i = 0; i < methods.length; i += 1) {
            methodName = methods[i];
            plugin[methodName] = pluginMethod(pluginName, methodName);
        }

        plugins[pluginName] = plugin;
        
        if (aliases[pluginName]) {
            plugins[aliases[pluginName]] = plugins[pluginName];
        }
    };

    // expose a promise, ready, that resolves once _didFinishLoad is called
    // _didFinishLoad is called by the native app when the page load is complete,
    // sending app-specific settings
    var _didFinishLoad;
    var ready = new window.Promise(function(resolve) {
        _didFinishLoad = function(settings, plugins, pluginMethods) {
            var i;

            if (!isLoadFinished) {
                isLoadFinished = true;
                window.savannah.settings = settings;
                for (i = 0; i < plugins.length; i += 1) {
                    _registerPlugin(plugins[i], pluginMethods[i]);
                }
            }
            resolve();
        };
    });

    // register aliases for plugins
    var alias = function(newAliases) {
        var names = Object.keys(newAliases);
        var i;
        var name;
        var alias;
        for (i = 0; i < names.length; i += 1) {
            name = names[i];
            alias = newAliases[name];
            // keep a record of the alias
            aliases[name] = alias;
            // if the plugin already exists, make alias point to it
            if (plugins[name]) {
                plugins[alias] = plugins[name];
            }
        }
    };

    // exposed functions
    window.savannah = {
        _fetchMessages: _fetchMessages,
        _callback: _callback,
        _didFinishLoad: _didFinishLoad,
        alias: alias,
        ready: ready,
        exec: exec,
        plugins: plugins,
        version: version
    };

}(window));

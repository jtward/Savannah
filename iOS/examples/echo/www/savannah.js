(function(window) {
    "use strict";
    var version = "0.11.0";

    // calls fn on the leading edge and, if called again,
    // the trailing edge, of the debounce window.
    var debounce = function(fn, wait) {
        var timeout,
            didCallAgain,
            later;

        later = function() {
            if (didCallAgain) {
                fn();
            }
            timeout = null;
            didCallAgain = false;
        };

        return function() {
            if (!timeout) {
                timeout = setTimeout(later, wait);
                setTimeout(fn, 0);
            }
            else {
                didCallAgain = true;
            }
        };
    };

    var Savannah = function (window) {
        var publicAPI = this,

            // a container for all the unresolved callbacks
            callbacks = {},

            // a container for promises
            promises = {},

            // a container for progress callbacks
            progressCallbacks = {},

            // a list of pending JS->Native messages.
            commandQueue = [],

            // a container for plugins
            plugins = {},

            // a hash of fully qualified plugin names to their aliases
            aliases = {},

            // a list of aliases
            aliasNames = [],

            // keeps track of whether load is finished
            isLoadFinished = false,

            // functions
            notifyNative,
            setNotifyNative,
            exec,
            fetchMessages,
            notifyProgress,
            callback,
            pluginMethod,
            registerPlugin,
            didFinishLoad,
            ready,
            alias,
            getIsLoadFinished;

        // notify the native app that there are commands waiting
        // send the command data if possible to avoid a round trip
        setNotifyNative = function() {
            notifyNative = debounce((function() {
                if (window.savannahJSI) {
                    // Android
                    return function() {
                        var commands;
                        if (commandQueue.length) {
                            // there could be inconsistency if _fetchMessages is called
                            // on exec before we clear the command queue, so clear the
                            // queue first
                            commands = JSON.stringify(commandQueue);
                            commandQueue.length = 0;
                            window.savannahJSI.exec(commands);
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
        };

        // IIFE; returns a function which is the entry point for all plugin execution
        exec = (function() {

            var callbackId = 1,
                promiseProgress;

                // keep a record of the given callback for progress for the given callback ID
            promiseProgress = function(callbackId, callback) {
                var callbacks = progressCallbacks[callbackId];
                if (!callbacks) {
                    callbacks = (progressCallbacks[callbackId] = []);
                }
                callbacks.push(callback);
            };

            // the real exec
            return function(successCallback, failCallback, service, action, actionArgs) {
                var tmpService,
                    command,
                    promise;

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
        fetchMessages = function() {
            // Each entry in commandQueue is a JSON string already.
            var json = JSON.stringify(commandQueue);
            commandQueue.length = 0;
            return json;
        };

        // call all progress listeners for the callback with the given arguments
        notifyProgress = function(callbackId, args) {
            var listeners = progressCallbacks[callbackId],
                i;
            
            for (i = 0; i < (listeners && listeners.length); i += 1) {
                listeners[i](args);
            }
        };

        // called when a response (success, fail or progress) is returned from the native app
        callback = function(callbackId, success, args, keepCallback) {
            var callback = callbacks[callbackId],
                promise = promises[callbackId];

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

        pluginMethod = function(pluginName, methodName) {
            return function() {
                var args = [Array.prototype.slice.call(arguments, 0)];
                args.unshift(methodName);
                args.unshift(pluginName);
                return exec.apply(null, args);
            };
        };

        // called by the native app to register a plugin
        registerPlugin = function(pluginName, methods) {
            var plugin = {},
                methodName,
                i;
            
            for (i = 0; i < methods.length; i += 1) {
                methodName = methods[i];
                plugin[methodName] = pluginMethod(pluginName, methodName);
            }

            plugins[pluginName] = plugin;
            
            if (aliases[pluginName]) {
                for (i = 0; i < aliases[pluginName].length; i += 1) {
                    plugins[aliases[pluginName][i]] = plugins[pluginName];
                }
            }
        };

        // expose a promise, ready, that resolves once didFinishLoad is called
        // didFinishLoad is called by the native app when the page load is complete,
        // sending app-specific settings
        var ready = new window.Promise(function(resolve) {
            didFinishLoad = function(settings, plugins, pluginMethods) {
                var i;

                if (!isLoadFinished) {
                    setNotifyNative();
                    isLoadFinished = true;
                    publicAPI.settings = settings;
                    for (i = 0; i < plugins.length; i += 1) {
                        registerPlugin(plugins[i], pluginMethods[i]);
                    }

                    // the aliases hash is no longer required
                    aliases = undefined;
                }
                resolve();
            };
        });

        // register aliases for plugins
        alias = function(newAliases) {
            var names = Object.keys(newAliases),
                i,
                name,
                alias;

            for (i = 0; i < names.length; i += 1) {
                name = names[i];
                alias = newAliases[name];

                if (aliasNames.indexOf(alias) === -1) {
                    // keep a record of the alias
                    aliasNames.push(alias);

                    // if the plugin already exists, make alias point to it
                    // otherwise store the relation so we can make a reference once
                    // the plugins are ready
                    if (plugins[name]) {
                        plugins[alias] = plugins[name];
                    }
                    else {
                        if (!aliases[name]) {
                            aliases[name] = [];
                        }
                        aliases[name].push(alias);
                    }
                }
                else {
                    throw "An alias \"" + alias + "\" has already been defined.";
                }
            }
        };

        getIsLoadFinished = function() {
            return isLoadFinished;
        };

        publicAPI._fetchMessages = fetchMessages;
        publicAPI._callback = callback;
        publicAPI._didFinishLoad = didFinishLoad;
        publicAPI._getIsLoadFinished = getIsLoadFinished;
        publicAPI.alias = alias;
        publicAPI.ready = ready;
        publicAPI.exec = exec;
        publicAPI.plugins = plugins;
        publicAPI.version = version;
    };

    window.savannah = new Savannah(window);

}(window));

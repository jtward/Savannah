describe("Savannah", function() {

    var createAndroidInstance;
    var createiOSInstance;

    (function() {
        var commonEnvironment = {
            Promise: window.Promise
        };

        createAndroidInstance = function() {
            var environment = _.extend({
                savannahJSI: {
                    exec: jasmine.createSpy("exec")
                }
            }, commonEnvironment);
            var savannah = new window.savannah.constructor(environment);
            environment.savannah = savannah;
            return {
                savannah: savannah,
                environment: environment
            };
        };

        createiOSInstance = function() {
            var environment = _.extend({
                location: ""
            }, commonEnvironment);
            var savannah = new window.savannah.constructor(environment);
            environment.savannah = savannah;
            return {
                savannah: savannah,
                environment: environment
            };
        };
    }());
    

    var androidSavannah = undefined;
    var androidEnvironment = undefined;
    var iOSSavannah = undefined;
    var iOSEnvironment = undefined;

    beforeEach(function() {
        var android = createAndroidInstance();
        androidSavannah = android.savannah;
        androidEnvironment = android.environment;
        var iOS = createiOSInstance();
        iOSSavannah = iOS.savannah;
        iOSEnvironment = iOS.environment;
    });

    it("should resolve the ready promise when ready", function(done) {
        var settings = {};
        var plugins = [];
        var pluginMethods = [];

        var androidReady = jasmine.createSpy('androidReady');
        androidSavannah.ready.then(androidReady);
        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);

        var iOSReady = jasmine.createSpy('iOSReady');
        iOSSavannah.ready.then(iOSReady);
        iOSSavannah._didFinishLoad(settings, plugins, pluginMethods);

        setTimeout(function() {
            expect(androidReady).toHaveBeenCalled();
            expect(iOSReady).toHaveBeenCalled();
            done();
        }, 1);
    });

    it("should expose settings", function() {
        var settings = {
            foo: "bar"
        };
        var plugins = [];
        var pluginMethods = [];

        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(androidSavannah.settings).toBe(settings);

        iOSSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(iOSSavannah.settings).toBe(settings);
    });

    it("should expose plugin methods", function() {
        var settings = {};
        var plugins = ["foo"];
        var pluginMethods = [["bar"]];

        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(typeof androidSavannah.plugins.foo.bar).toBe("function");

        iOSSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(typeof iOSSavannah.plugins.foo.bar).toBe("function");
    });

    it("should alias plugins", function() {
        var settings = {};
        var plugins = ["foo"];
        var pluginMethods = [["bar"]];

        androidSavannah.alias({
            "foo": "bar"
        });
        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(typeof androidSavannah.plugins.bar.bar).toBe("function");

        iOSSavannah.alias({
            "foo": "bar"
        });
        iOSSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(typeof iOSSavannah.plugins.bar.bar).toBe("function");
    });

    it("should not allow overriding plugin aliases", function() {
        var settings = {};
        var plugins = ["foo", "baz"];
        var pluginMethods = [["bar"], ["qux"]];

        androidSavannah.alias({
            "foo": "bar"
        });

        var thrownException;

        try {
            androidSavannah.alias({
                "baz": "bar"
            });
        }
        catch (e) {
            thrownException = e;
        }

        expect(thrownException).toBe("An alias \"bar\" has already been defined.");
    });

    it("should allow multiple aliases for a plugin", function() {
        var settings = {};
        var plugins = ["foo"];
        var pluginMethods = [["bar"]];

        androidSavannah.alias({
            "foo": "bar"
        });
        androidSavannah.alias({
            "foo": "baz"
        });

        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(typeof androidSavannah.plugins.bar.bar).toBe("function");
        expect(androidSavannah.plugins.bar).toBe(androidSavannah.plugins.baz);
    });

    it("should allow aliases to be defined before or after load", function() {
        var settings = {};
        var plugins = ["foo"];
        var pluginMethods = [["bar"]];

        androidSavannah.alias({
            "foo": "bar"
        });

        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);
        expect(typeof androidSavannah.plugins.bar.bar).toBe("function");
        expect(androidSavannah.plugins.baz).toBeUndefined();

        androidSavannah.alias({
            "foo": "baz"
        });

        expect(androidSavannah.plugins.bar).toBe(androidSavannah.plugins.baz);
    });

    it("should not have initial messages", function(done) {
        var settings = {};
        var plugins = [];
        var pluginMethods = [];

        iOSSavannah._didFinishLoad(settings, plugins, pluginMethods);
        iOSSavannah.ready.then(function() {
            var messagesString = iOSSavannah._fetchMessages();
            expect(messagesString).toBe("[]");
            done();
        });
    });

    it("should call the native layer on exec", function(done) {
        var settings = {};
        var plugins = ["foo"];
        var pluginMethods = [["bar"]];

        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);
        androidSavannah.ready.then(function() {
            androidSavannah.plugins.foo.bar({});
        });

        iOSSavannah._didFinishLoad(settings, plugins, pluginMethods);
        iOSSavannah.ready.then(function() {
            iOSSavannah.plugins.foo.bar({});
        });

        setTimeout(function() {
            var messagesString, messages;
            // Android
            expect(androidEnvironment.savannahJSI.exec.calls.count()).toBe(1);
            messagesString = androidEnvironment.savannahJSI.exec.calls.argsFor(0)[0];
            expect(typeof messagesString).toBe("string");
            messages = JSON.parse(messagesString);

            expect(messages.length).toBe(1);
            expect(messages[0].length).toBe(4);
            expect(typeof messages[0][0]).toBe("number");
            expect(messages[0][1]).toBe("foo");
            expect(messages[0][2]).toBe("bar");
            expect(JSON.stringify(messages[0][3])).toBe("[{}]");

            // iOS
            expect(iOSEnvironment.location).toBe("/!svnh_exec?");
            messagesString = iOSSavannah._fetchMessages();
            expect(typeof messagesString).toBe("string");
            messages = JSON.parse(messagesString);

            expect(messages.length).toBe(1);
            expect(messages[0].length).toBe(4);
            expect(typeof messages[0][0]).toBe("number");
            expect(messages[0][1]).toBe("foo");
            expect(messages[0][2]).toBe("bar");
            expect(JSON.stringify(messages[0][3])).toBe("[{}]");
            done();
        }, 100);
    });

    it("should pass all commands on exec, but may bundle to reduce exec calls", function(done) {
        var settings = {};
        var plugins = ["foo"];
        var pluginMethods = [["bar"]];

        [androidSavannah, iOSSavannah].forEach(function(savannah) {
            savannah._didFinishLoad(settings, plugins, pluginMethods);
            savannah.ready.then(function() {
                savannah.plugins.foo.bar({});
                setTimeout(function() {
                    savannah.plugins.foo.bar({});
                }, 1);
                setTimeout(function() {
                    savannah.plugins.foo.bar({});
                }, 1);
            });
        });

        var androidMessagesSent = 0;
        var iOSMessagesSent = 0;

        var maybeDone = function() {
            if (androidMessagesSent === 3 && iOSMessagesSent === 3) {
                var remainingAndroidMessages = androidSavannah._fetchMessages();
                expect(remainingAndroidMessages).toBe("[]");

                var remainingiOSMessages = iOSSavannah._fetchMessages();
                expect(remainingiOSMessages).toBe("[]");

                done();
                return true;
            }
            return false;
        };

        androidEnvironment.savannahJSI.exec.and.callFake(function(messagesString) {
            var message = JSON.parse(messagesString);
            androidMessagesSent += message.length
            maybeDone();
        });

        var checkLocation = function() {
            requestAnimationFrame(function() {
                var message;
                if (iOSEnvironment.location === "/!svnh_exec?") {
                    iOSEnvironment.location = "";
                    message = JSON.parse(iOSSavannah._fetchMessages());
                    iOSMessagesSent += message.length;
                    maybeDone() || checkLocation();
                }
                else {
                    checkLocation();
                }
            });
        };

        checkLocation();
    });

    it('should use unique IDs', function(done) {
        var settings = {};
        var plugins = ["foo"];
        var pluginMethods = [["bar"]];

        androidSavannah._didFinishLoad(settings, plugins, pluginMethods);
        androidSavannah.ready.then(function() {
            androidSavannah.plugins.foo.bar({});
            androidSavannah.plugins.foo.bar({});
        });

        setTimeout(function() {
            messagesString = androidEnvironment.savannahJSI.exec.calls.argsFor(0)[0];
            var messages = JSON.parse(messagesString);
            expect(messages.length).toBe(2);
            expect(messages[0][1]).not.toBe(messages[1][0]);
            done();
        }, 100);
    });
});

var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'DemoPlugin', 'coolMethod', [arg0]);
};

exports.initializeConnection = function(arg0, arg1, success, error) {
    exec(success, error, 'DemoPlugin', 'initializeConnection', [arg0, arg1]);
}

exports.saleTransaction = function(arg0, success, error) {
    exec(success, error, 'DemoPlugin', 'saleTransaction', [arg0]);
}
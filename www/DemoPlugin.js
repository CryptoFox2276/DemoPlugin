var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'DemoPlugin', 'coolMethod', [arg0]);
};

exports.initializeConnection = function(arg0, success, error) {
    exec(success, error, 'DemoPlugin', 'initializeConnection', [arg0]);
}

exports.saleTransaction = function(arg0, success, error) {
    exec(success, error, 'DemoPlugin', 'saleTransaction', [arg0]);
}

exports.saleTransactionWithTip = function(args, success, error) {
    exec(success, error, "DemoPlugin", 'saleTransactionWithTip', [args]);
}

exports.refundTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'refundTransaction', [args]);
}

exports.voidTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'voidTransaction', [args]);
}

exports.preAuthTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'preAuthTransaction', [args]);
}

exports.deletePreAuthTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'deletePreAuthTransaction', [args]);
}

exports.authCompletionTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'authCompletionTransaction', [args]);
}

exports.cancelTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'cancelTransaction', [args]);
}

exports.restartTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'restartTransaction', [args]);
}

exports.mailOrderTransaction = function(args, success, error) {
    exec(success, error, 'DemoPlugin', 'mailOrderTransaction', [args]);
}
var exec = require('cordova/exec');

exports.init = function(success, error) {
    exec(success, error, "SecureKeyStore", "init");
};

exports.createKeys = function(success, error) {
    exec(success, error, "SecureKeyStore", "createKeys");
};

exports.getKey = function(arg0, success, error) {
    exec(success, error, "SecureKeyStore", "getKey", [arg0]);
};

exports.signData = function(arg0, success, error) {
    exec(success, error, "SecureKeyStore", "signData");
};

exports.verifyData = function(success, error) {
    exec(success, error, "SecureKeyStore", "verifyData");
};

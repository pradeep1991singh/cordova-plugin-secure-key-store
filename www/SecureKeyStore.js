var exec = require('cordova/exec');

exports.getKey = function(arg0, success, error) {
    exec(success, error, "SecureKeyStore", "getKey", [arg0]);
};

exports.setKey = function(success, error) {
    exec(success, error, "SecureKeyStore", "setKey");
};

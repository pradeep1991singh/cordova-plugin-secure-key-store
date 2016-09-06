// JS interface

window.SksEncrypt = function(alias, input, success, error) {
    cordova.exec(success, error, "SecureKeyStore", "encrypt", [alias, input]);
};

window.SksDecrypt = function(alias, success, error) {
    cordova.exec(success, error, "SecureKeyStore", "decrypt", [alias]);
};
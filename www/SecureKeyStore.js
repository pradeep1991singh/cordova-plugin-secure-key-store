// JS interface

window.keyEncryptInput = function(alias, input, success, error) {
    cordova.exec(success, error, "SecureKeyStore", "encrypt", [alias, input]);
};

window.keyDecryptInput = function(alias, cipherText, success, error) {
    cordova.exec(success, error, "SecureKeyStore", "decrypt", [alias, cipherText]);
};
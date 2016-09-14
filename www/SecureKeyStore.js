// JS interface   

switch (cordova.platformId) {
case 'ios':
    window.SksEncrypt = function(alias, input, success, error) {
        cordova.exec(success, error, "SecureKeyStore", "set", [key, value, useTouchID]);
    };

    window.SksDecrypt = function(alias, success, error) {
        cordova.exec(success, error, "SecureKeyStore", "get", [key, touchIDMessage]);
    };

    window.SkRemove = function(alias, success, error) {
        cordova.exec(success, error, "SecureKeyStore", "remove", [key]);
    };    
    break;

case 'android':
    window.SksEncrypt = function(alias, input, success, error) {
        cordova.exec(success, error, "SecureKeyStore", "encrypt", [alias, input]);
    };

    window.SksDecrypt = function(alias, success, error) {
        cordova.exec(success, error, "SecureKeyStore", "decrypt", [alias]);
    };
    break;
}
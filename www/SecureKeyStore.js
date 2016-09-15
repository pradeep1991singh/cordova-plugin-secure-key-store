// JS interface
var exec = require('cordova/exec');

var SecureKeyStore = {
	serviceName: "SecureKeyStore",

	set: function(success, error, key, value, useTouchID) {
		exec(success, error, this.serviceName, "set", [key, value, useTouchID]);
	},

	get: function(success, error, key, touchIDMessage) {
		exec(success, error, this.serviceName, "get", [key, touchIDMessage]);
	},

	remove: function(success, error, key) {
		exec(success, error, this.serviceName, "remove", [key]);
	}
};

module.exports = SecureKeyStore;
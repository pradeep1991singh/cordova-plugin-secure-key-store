// JS interface
var exec = require('cordova/exec');

var SecureKeyStore = {
	serviceName: "SecureKeyStore",

	set: function(success, error, key, value) {
		exec(success, error, this.serviceName, "set", [key, value]);
	},

	get: function(success, error, key) {
		exec(success, error, this.serviceName, "get", [key]);
	},

	remove: function(success, error, key) {
		exec(success, error, this.serviceName, "remove", [key]);
	}
};

module.exports = SecureKeyStore;
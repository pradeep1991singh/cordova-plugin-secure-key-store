# cordova-plugin-secure-key-store
Cordova plugin for securely saving keys, passwords or strings on devices.

## Installation

The plugin can be installed via the Cordova command line interface:

```sh
cordova plugin add cordova-plugin-secure-key-store
```

or via github

```sh
cordova plugin add https://github.com/pradeep1991singh/cordova-plugin-secure-key-store
```

## Usage

This plugin will add three new methods to window scope, for saving sensitive data, retrieving saved data and for removing data.

- For saving use `cordova.plugins.SecureKeyStore.set` 

```js
cordova.plugins.SecureKeyStore.set(function (res) {
  console.log(res); // res - string securely stored
}, function (error) {
  console.log(error);
}, "key", 'string to encrypt');
```

- For retrieving use `cordova.plugins.SecureKeyStore.get`.

```js
cordova.plugins.SecureKeyStore.get(function (res) {
  console.log(res); // res - string retrieved
}, function (error) {
  console.log(error);
}, "key");
```
- And for removing `cordova.plugins.SecureKeyStore.remove`

```js
cordova.plugins.SecureKeyStore.remove(function (res) {
  console.log(res); // res - string removed
}, function (error) {
  console.log(error);
}, "key");
```

## License

MIT License

Copyright (c) 2017 pradeep singh

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

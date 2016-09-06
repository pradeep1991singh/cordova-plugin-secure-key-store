# cordova-plugin-secure-key-store
Cordova plugin for securely saving keys to device

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

- This plugin will add two new methods to window scope, one is for encrypting and other for decrypting keys.

- For encrypting string `window.SksEncrypt` and  for decrypting `window.SksDecrypt` :

```js
window.SksEncrypt("key", 'string to encrypt', function(response) {
  console.log(response); // response - encrypted string
}, function(error) {
    console.log(error);
});
```

```js
window.SksDecrypt("key", function(response) {
  console.log(response); // response - decrypted string
}, function(error) {
  console.log(error);
});
```

## License

ISC

Copyright (c) 2016 pradeep singh

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.



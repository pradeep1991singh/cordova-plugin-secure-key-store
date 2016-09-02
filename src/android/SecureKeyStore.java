package com.securekeystore.plugins;

import java.lang.String;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class SecureKeyStore extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("setKey")) {
            String key = args.getString(0);
            this.setKey(key, callbackContext);
            return true;
        }

        if (action.equals("getKey")) {
            String key = args.getString(0);
            this.getKey(callbackContext);
            return true;
        }

        return false;
    }

    private void setKey(String key, CallbackContext callbackContext) {
//        if (message != null && message.length() > 0) {
//            callbackContext.success(message);
//        } else {
//            callbackContext.error("Expected one non-empty string argument.");
//        }
//        private static void writeSecretKeyToKeystore(SecretKey secretKey, Context context) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
//                KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            keyStore.setEntry(
                    "signPk",
                    new KeyStore.SecretKeyEntry(key),
                    new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockMode(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getKey( CallbackContext callbackContext) {
        SecretKey keyStoreKey = (SecretKey) keyStore.getKey("signPk", null);
        if (keyStoreKey != null && keyStoreKey.length() > 0) {
            callbackContext.success(keyStoreKey);
        } else {
            callbackContext.error("No key found, try to login again.");
        }
    }
}

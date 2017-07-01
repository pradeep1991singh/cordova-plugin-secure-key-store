package com.securekeystore.plugin;

// Secure key store main class

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.util.Base64;
import android.security.KeyPairGeneratorSpec;
import android.os.Build;

import java.security.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.StringBuffer;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class SecureKeyStore extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("set")) {
            String alias = args.getString(0);
            String input = args.getString(1);
            this.encrypt(alias, input, callbackContext);
            return true;
        }

        if (action.equals("get")) {
            String alias = args.getString(0);
            this.decrypt(alias, callbackContext);
            return true;
        }

        if (action.equals("remove")) {
            String alias = args.getString(0);
            this.removeKeyFile(alias, callbackContext);
            return true;
        }

        return false;
    }

    private void encrypt(String alias, String input, CallbackContext callbackContext) {

        try {

            KeyStore keyStore = KeyStore.getInstance(getKeyStore());
            keyStore.load(null);

            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getContext()).setAlias(alias)
                        .setSubject(new X500Principal("CN=" + alias)).setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime()).setEndDate(end.getTime()).build();

                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", getKeyStore());
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();

                Log.i(Constants.TAG, "created new key pairs");
            }

            PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();

            if (input.isEmpty()) {
                Log.d(Constants.TAG, "Exception: input text is empty");
                return;
            }

            Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            cipherOutputStream.write(input.getBytes("UTF-8"));
            cipherOutputStream.close();
            byte[] vals = outputStream.toByteArray();

            // writing key to storage
            KeyStorage.writeValues(getContext(), alias, vals);
            Log.i(Constants.TAG, "key created and stored successfully");
            callbackContext.success("key created and stored successfully");

        } catch (Exception e) {
            Log.e(Constants.TAG, "Exception: " + e.getMessage());
            callbackContext.error(
                    "{\"code\":9,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
        }

    }

    private void decrypt(String alias, CallbackContext callbackContext) {

        try {

            KeyStore keyStore = KeyStore.getInstance(getKeyStore());
            keyStore.load(null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);

            Cipher output = Cipher.getInstance(Constants.RSA_ALGORITHM);
            output.init(Cipher.DECRYPT_MODE, privateKey);
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(KeyStorage.readValues(getContext(), alias)), output);

            ArrayList<Byte> values = new ArrayList<Byte>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }
            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
            callbackContext.success(finalText);

        } catch (Exception e) {
            Log.e(Constants.TAG, "Exception: " + e.getMessage());
            callbackContext.error(
                    "{\"code\":1,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
        }
    }

    private void removeKeyFile(String alias, CallbackContext callbackContext) {
        try {
            KeyStorage.resetValues(getContext(), alias);
            Log.i(Constants.TAG, "keys removed successfully");
            callbackContext.success("keys removed successfully");

        } catch (Exception e) {
            Log.e(Constants.TAG, "Exception: " + e.getMessage());
            callbackContext.error(
                    "{\"code\":6,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
        }
    }

    private Context getContext() {
        return cordova.getActivity().getApplicationContext();
    }

    private String getKeyStore() {
        try {
            KeyStore.getInstance(Constants.KEYSTORE_PROVIDER_1);
            return Constants.KEYSTORE_PROVIDER_1;
        } catch (Exception err) {
            try {
                KeyStore.getInstance(Constants.KEYSTORE_PROVIDER_2);
                return Constants.KEYSTORE_PROVIDER_2;
            } catch (Exception e) {
                return Constants.KEYSTORE_PROVIDER_3;
            }
        }
    }

}

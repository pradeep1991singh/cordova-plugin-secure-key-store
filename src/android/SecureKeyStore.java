package com.securekeystore.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.util.Base64;
import android.security.KeyPairGeneratorSpec;

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

        if (action.equals("encrypt")) {
            String alias = args.getString(0);
            String input = args.getString(1);
            this.encrypt(alias, input, callbackContext);
            return true;
        }

        if (action.equals("decrypt")) {
            String alias = args.getString(0);
            this.decrypt(alias, callbackContext);
            return true;
        }

        return false;
    }

    private void encrypt(String alias, String input, CallbackContext callbackContext) {

        try {

            KeyStore keyStore = KeyStore.getInstance(Constants.KEYSTORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(
                        getContext())
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=" + alias))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", Constants.KEYSTORE);
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
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, cipher);
            cipherOutputStream.write(input.getBytes("UTF-8"));
            cipherOutputStream.close();
            byte[] vals = outputStream.toByteArray();

            // writing key to storage
            KeyStorage.writeValues(getContext(), vals);
            Log.i(Constants.TAG, "key created and stored successfully");
            callbackContext.success("key created and stored successfully");

        } catch (Exception e) {
            Log.e(Constants.TAG, "Exception: "  + e.getMessage());
            callbackContext.error("Exception: "  + e.getMessage());
        }

    }

    private void decrypt(String alias, CallbackContext callbackContext) {

        try {

            KeyStore keyStore = KeyStore.getInstance(Constants.KEYSTORE);
            keyStore.load(null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);


            Cipher output = Cipher.getInstance(Constants.RSA_ALGORITHM);
            output.init(Cipher.DECRYPT_MODE, privateKey);
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(KeyStorage.readValues(getContext())), output);

            ArrayList<Byte> values = new ArrayList<Byte>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }
            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
            callbackContext.success(finalText);

        } catch (Exception e) {
            Log.e(Constants.TAG, "Exception: "  + e.getMessage());
            callbackContext.error("Exception: "  + e.getMessage());
        }
    }

    private Context getContext(){
        return cordova.getActivity().getApplicationContext();
    }

}

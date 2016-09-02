package com.securekeystore.plugins;

import java.lang.String;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import javax.crypto.SecretKey;

import android.content.Context;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyGenParameterSpec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.security.auth.x500.X500Principal;


/**
 * This class echoes a string called from JavaScript.
 */
public class SecureKeyStore extends CordovaPlugin {

    public static final String TAG = "SecureKeyStore";


    public static final String SAMPLE_ALIAS = "myKey";

    // Some sample data to sign, and later verify using the generated signature.
    public static final String SAMPLE_INPUT="Hello, Android!";

    // Just a handy place to store the signature in between signing and verifying.
    public String mSignatureStr = null;

    // You can store multiple key pairs in the Key Store.  The string used to refer to the Key you
    // want to store, or later pull, is referred to as an "alias" in this case, because calling it
    // a key, when you use it to retrieve a key, would just be irritating.
    private String mAlias = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("init")) {
            setAlias(SAMPLE_ALIAS);
            return true;
        }

        if (action.equals("createKeys")) {
            createKeys();
            return true;
        }

        if (action.equals("signData")) {
            mSignatureStr = signData(SAMPLE_INPUT);
//            SecretKey key = args.getString(0);
//            this.setKey(key, callbackContext);
            return true;
        }

        if (action.equals("verifyData")) {
            boolean verified = false;
            if (mSignatureStr != null) {
                verified = verifyData(SAMPLE_INPUT, mSignatureStr);
            }
//            String key = args.getString(0);
//            this.getKey(callbackContext);
            return true;
        }

        return false;
    }

//    private void setKey(SecretKey secretKey, CallbackContext callbackContext) {
//        try {
//            keyStore = KeyStore.getInstance("AndroidKeyStore");
//            keyStore.load(null);
//            keyStore.setEntry(
//                    "signPk",
//                    new KeyStore.SecretKeyEntry(secretKey),
//                    new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
//                            .setBlockMode(KeyProperties.BLOCK_MODE_GCM)
//                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//                            .build());
//        } catch (e) {
//            e.printStackTrace();
//        }
////        catch (KeyStoreException e) {
////            e.printStackTrace();
////        } catch (CertificateException e) {
////            e.printStackTrace();
////        } catch (NoSuchAlgorithmException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//
//    private void getKey( CallbackContext callbackContext) {
//        keyStore = KeyStore.getInstance("AndroidKeyStore");
//        SecretKey keyStoreKey = (SecretKey) keyStore.getKey("signPk", null);
//        if (keyStoreKey != null && keyStoreKey.length() > 0) {
//            callbackContext.success(keyStoreKey);
//        } else {
//            callbackContext.error("No key found, try to login again.");
//        }
//    }

    /**
     * Creates a public and private key and stores it using the Android Key Store, so that only
     * this application will be able to access the keys.
     */
    public void createKeys() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        try {
            // Create a start and end time, for the validity range of the key pair that's about to be
            // generated.
//        Calendar start = new GregorianCalendar();
//        Calendar end = new GregorianCalendar();
//        end.add(Calendar.YEAR, 1);
//
//
//        // The KeyPairGeneratorSpec object is how parameters for your key pair are passed
//        // to the KeyPairGenerator.  For a fun home game, count how many classes in this sample
//        // start with the phrase "KeyPair".
//        KeyPairGeneratorSpec spec =
//                new KeyPairGeneratorSpec.Builder(context)
//                        // You'll use the alias later to retrieve the key.  It's a key for the key!
//                        .setAlias(mAlias)
//                                // The subject used for the self-signed certificate of the generated pair
//                        .setSubject(new X500Principal("CN=" + mAlias))
//                                // The serial number used for the self-signed certificate of the
//                                // generated pair.
//                        .setSerialNumber(BigInteger.valueOf(1337))
//                                // Date range of validity for the generated pair.
//                        .setStartDate(start.getTime())
//                        .setEndDate(end.getTime())
//                        .build();
//
//        // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
//        // and the KeyStore.  This example uses the AndroidKeyStore.
//        KeyPairGenerator kpGenerator = KeyPairGenerator
//                .getInstance("RSA",
//                        "AndroidKeyStore");
//        kpGenerator.initialize(spec);
//        KeyPair kp = kpGenerator.generateKeyPair();

        /*
         * Generate a new EC key pair entry in the Android Keystore by
         * using the KeyPairGenerator API. The private key can only be
         * used for signing or verification and only with SHA-256 or
         * SHA-512 as the message digest.
         */
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            kpg.initialize(new KeyGenParameterSpec.Builder(
                    mAlias,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256,
                            KeyProperties.DIGEST_SHA512)
                    .build());

            KeyPair kp = kpg.generateKeyPair();

            Log.d(TAG, "Public Key is: " + kp.getPublic().toString());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error", e);
            return false;
        }
    }

    public String signData(String inputStr) throws KeyStoreException,
            UnrecoverableEntryException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, IOException, CertificateException {
        try {
            byte[] data = inputStr.getBytes();

            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");

            // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
            // to call "load", or it'll crash.
            ks.load(null);

            // Load the key pair from the Android Key Store
            KeyStore.Entry entry = ks.getEntry(mAlias, null);

        /* If the entry is null, keys were never stored under this alias.
         * Debug steps in this situation would be:
         * -Check the list of aliases by iterating over Keystore.aliases(), be sure the alias
         *   exists.
         * -If that's empty, verify they were both stored and pulled from the same keystore
         *   "AndroidKeyStore"
         */
            if (entry == null) {
                Log.w(TAG, "No key found under alias: " + mAlias);
                Log.w(TAG, "Exiting signData()...");
                return null;
            }

        /* If entry is not a KeyStore.PrivateKeyEntry, it might have gotten stored in a previous
         * iteration of your application that was using some other mechanism, or been overwritten
         * by something else using the same keystore with the same alias.
         * You can determine the type using entry.getClass() and debug from there.
         */
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                Log.w(TAG, "Exiting signData()...");
                return null;
            }

            // This class doesn't actually represent the signature,
            // just the engine for creating/verifying signatures, using
            // the specified algorithm.
            Signature s = Signature.getInstance("SHA256withRSA");

            // Initialize Signature using specified private key
            s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());

            // Sign the data, store the result as a Base64 encoded String.
            s.update(data);
            byte[] signature = s.sign();
            String result = Base64.encodeToString(signature, Base64.DEFAULT);

            return result;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error", e);
            return false;
        }
    }

    public boolean verifyData(String input, String signatureStr) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableEntryException, InvalidKeyException, SignatureException {
        try {
            byte[] data = input.getBytes();
            byte[] signature;

            // Make sure the signature string exists.  If not, bail out, nothing to do.

            if (signatureStr == null) {
                Log.w(TAG, "Invalid signature.");
                Log.w(TAG, "Exiting verifyData()...");
                return false;
            }

            try {
                // The signature is going to be examined as a byte array,
                // not as a base64 encoded string.
                signature = Base64.decode(signatureStr, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                // signatureStr wasn't null, but might not have been encoded properly.
                // It's not a valid Base64 string.
                return false;
            }

            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");

            // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
            // to call "load", or it'll crash.
            ks.load(null);

            // Load the key pair from the Android Key Store
            KeyStore.Entry entry = ks.getEntry(mAlias, null);

            if (entry == null) {
                Log.w(TAG, "No key found under alias: " + mAlias);
                Log.w(TAG, "Exiting verifyData()...");
                return false;
            }

            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
                return false;
            }

            // This class doesn't actually represent the signature,
            // just the engine for creating/verifying signatures, using
            // the specified algorithm.
            Signature s = Signature.getInstance("SHA256withRSA");

            // Verify the data.
            s.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
            s.update(data);
            return s.verify(signature);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error", e);
            return false;
        }

    }

    public void setAlias(String alias) {
        mAlias = alias;
    }

}

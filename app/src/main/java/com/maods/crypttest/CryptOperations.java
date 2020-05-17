package com.maods.crypttest;

import android.text.TextUtils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static javax.crypto.Cipher.*;

public class CryptOperations {
    public static final String MANAGE_KEYS="generate keys";
    public static final String CRYPT_DECRYPT="crypt and decrypt";
    public static final String SIGN_AND_VERIFY="sign and verify";
    //public static final String SIGN_AND_VERIFY_SM2="sign and verify (SM2)";

    public static final boolean IS_SUPPORT_CHINESE_STD = false;

    private static KeyPair sKeyPair;
    static{
        Provider provider=new BouncyCastleProvider();
        Security.addProvider(provider);
        for (Provider.Service service : provider.getServices()) {
            System.out.println(service.getType() + ": "
                    + service.getAlgorithm());
        }
    }

    public static KeyPair getKeyPair(/*boolean createNew,*/String alg){
        /*if(!createNew) {
            if (sKeyPair != null) {
                return sKeyPair;
            }
        }*/
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }/*catch(NoSuchProviderException e){
            e.printStackTrace();
        }*/
        keyPairGenerator.initialize(256,new SecureRandom());
        sKeyPair = keyPairGenerator.generateKeyPair();
        return sKeyPair;
    }

    public static byte[] encrypt(byte[]src, PublicKey pubKey){
        try {
            Cipher encrypter = getInstance("RSA");
            encrypter.init(ENCRYPT_MODE, pubKey);
            return encrypter.doFinal(src);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } /*catch (NoSuchProviderException e) {
            e.printStackTrace();
        }*/catch (java.security.InvalidKeyException e){
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[]src, PrivateKey priKey) {
        Cipher decrypter = null;
        try {
            decrypter = Cipher.getInstance("RSA");
            decrypter.init(Cipher.DECRYPT_MODE, priKey);
            return decrypter.doFinal(src);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } /*catch (NoSuchProviderException e) {
            e.printStackTrace();
        }*/catch (InvalidKeyException e){
        e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] signData(String algorithm, byte[] data, PrivateKey key) throws Exception {
        Signature signer = Signature.getInstance(algorithm);
        signer.initSign(key);
        signer.update(data);
        return (signer.sign());
    }

    public static boolean verifySign(String algorithm, byte[] data, PublicKey key, byte[] sig) throws Exception {
        Signature signer = Signature.getInstance(algorithm);
        signer.initVerify(key);
        signer.update(data);
        return (signer.verify(sig));
    }

    public static String decodeBASE64ToStr(byte[] data) {
        return new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
    }

    public static String encodeBASE64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}

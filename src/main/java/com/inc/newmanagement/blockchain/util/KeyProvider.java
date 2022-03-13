package com.inc.newmanagement.blockchain.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class KeyProvider {

    private KeyProvider() {
        throw new IllegalStateException("utility class");
    }

    public static KeyPair generateECKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        final ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");
        keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);

        return keyPairGenerator.generateKeyPair();
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] applyECDSASignature(PrivateKey privateKey, String data) throws
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        final Signature ecdsaSignature = Signature.getInstance("ECDSA", "BC");
        ecdsaSignature.initSign(privateKey);
        ecdsaSignature.update(data.getBytes());

        return ecdsaSignature.sign();
    }

    public static boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature) throws
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        final Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data.getBytes());

        return ecdsaVerify.verify(signature);
    }

    public static String getBase64Encoding(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}

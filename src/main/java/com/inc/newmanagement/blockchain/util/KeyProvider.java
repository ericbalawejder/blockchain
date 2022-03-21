package com.inc.newmanagement.blockchain.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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

    public static String getBase64Encoding(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}

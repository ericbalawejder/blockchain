package com.inc.newmanagement.blockchain.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

public class Signature {

    private Signature() {
        throw new IllegalStateException("utility class");
    }

    public static byte[] applyECDSA(PrivateKey privateKey, String data) throws
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        final java.security.Signature ecdsaSignature = java.security.Signature.getInstance("ECDSA", "BC");
        ecdsaSignature.initSign(privateKey);
        ecdsaSignature.update(data.getBytes());

        return ecdsaSignature.sign();
    }

    public static boolean verifyECDSA(PublicKey publicKey, String data, byte[] signature) throws
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        final java.security.Signature ecdsaVerify = java.security.Signature.getInstance("ECDSA", "BC");
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data.getBytes());

        return ecdsaVerify.verify(signature);
    }

}

package com.inc.newmanagement.blockchain.util;

import org.springframework.security.crypto.codec.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Hash {

    private static final String ALGORITHM = "SHA-256";

    private Hash() {
        throw new IllegalStateException("utility class");
    }

    public static String sha256Hex(String clearText) throws NoSuchAlgorithmException {
        final byte[] bytes = MessageDigest.getInstance(ALGORITHM)
                .digest(clearText.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(bytes));
    }

    public static String sha256Base64(String clearText) throws NoSuchAlgorithmException {
        final byte[] messageDigest = MessageDigest.getInstance(ALGORITHM)
                .digest(clearText.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(messageDigest));
    }

}

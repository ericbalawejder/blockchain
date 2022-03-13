package com.inc.newmanagement.blockchain.wallet;

import com.inc.newmanagement.blockchain.util.KeyProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

public class Wallet {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public Wallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        final KeyPair keyPair = KeyProvider.generateECKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return publicKey.equals(wallet.publicKey) && privateKey.equals(wallet.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publicKey, privateKey);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "publicKey=" + publicKey +
                ", privateKey=" + privateKey +
                '}';
    }

}

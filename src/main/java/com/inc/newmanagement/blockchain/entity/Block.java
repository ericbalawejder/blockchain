package com.inc.newmanagement.blockchain.entity;

import com.inc.newmanagement.blockchain.util.Hash;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;

public class Block {

    private final String data;
    private final String previousHash;
    private final long timeStamp;
    private final int nonce;
    private final String hash;

    public Block(String data, String previousHash) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = Instant.now().toEpochMilli();
        this.nonce = generateNonce();
        this.hash = calculateHash();
    }

    public Block(Block block, int nonce) throws NoSuchAlgorithmException {
        this.data = block.getData();
        this.previousHash = block.getPreviousHash();
        this.timeStamp = block.getTimeStamp();
        this.nonce = nonce;
        this.hash = calculateHash();
    }

    public Block(Block block) {
        this.data = block.getData();
        this.previousHash = block.getPreviousHash();
        this.timeStamp = block.getTimeStamp();
        this.nonce = block.getNonce();
        this.hash = block.getHash();
    }

    public String calculateHash() throws NoSuchAlgorithmException {
        return Hash.sha256Hex(this.previousHash + this.timeStamp +
                nonce + this.data);
    }

    public String getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return timeStamp == block.timeStamp &&
                hash.equals(block.hash) &&
                previousHash.equals(block.previousHash) &&
                data.equals(block.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, previousHash, data, timeStamp);
    }

    @Override
    public String toString() {
        return "Block{" +
                "hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", data='" + data + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }

    private int generateNonce() throws NoSuchAlgorithmException, NoSuchProviderException {
        final SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
        final byte[] randomBytes = new byte[128];
        secureRandomGenerator.nextBytes(randomBytes);
        return secureRandomGenerator.nextInt();
    }

}

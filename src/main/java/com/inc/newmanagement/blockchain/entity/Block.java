package com.inc.newmanagement.blockchain.entity;

import com.inc.newmanagement.blockchain.transaction.Transaction;
import com.inc.newmanagement.blockchain.util.Hash;
import com.inc.newmanagement.blockchain.util.Util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Block {

    private String hash;
    private String merkleRoot;
    private int nonce;

    private final String previousHash;
    private final List<Transaction> transactions;
    private final long timeStamp;

    public Block(String previousHash) throws NoSuchAlgorithmException {
        this.previousHash = previousHash;
        this.transactions = new ArrayList<>();
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() throws NoSuchAlgorithmException {
        return Hash.sha256Hex(previousHash + timeStamp + nonce + merkleRoot);
    }

    public void mineBlock(int difficulty) throws NoSuchAlgorithmException {
        merkleRoot = Util.getMerkleRoot(transactions);
        String target = Util.getDifficultyString(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public void addTransaction(Transaction transaction) throws NoSuchAlgorithmException,
            SignatureException, NoSuchProviderException, InvalidKeyException {

        if (transaction == null) {
            return;
        }
        if ((!"0".equals(previousHash))) {
            if ((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public int getNonce() {
        return nonce;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return nonce == block.nonce &&
                timeStamp == block.timeStamp &&
                hash.equals(block.hash) &&
                Objects.equals(merkleRoot, block.merkleRoot) &&
                Objects.equals(previousHash, block.previousHash) &&
                Objects.equals(transactions, block.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, merkleRoot, nonce, previousHash, transactions, timeStamp);
    }

    @Override
    public String toString() {
        return "Block{" +
                "hash='" + hash + '\'' +
                ", merkleRoot='" + merkleRoot + '\'' +
                ", nonce=" + nonce +
                ", previousHash='" + previousHash + '\'' +
                ", transactions=" + transactions +
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

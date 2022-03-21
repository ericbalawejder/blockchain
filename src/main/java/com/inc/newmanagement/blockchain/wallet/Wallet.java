package com.inc.newmanagement.blockchain.wallet;

import com.inc.newmanagement.blockchain.chain.Blockchain;
import com.inc.newmanagement.blockchain.transaction.Transaction;
import com.inc.newmanagement.blockchain.transaction.TransactionInput;
import com.inc.newmanagement.blockchain.transaction.TransactionOutput;
import com.inc.newmanagement.blockchain.util.KeyProvider;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final Map<String, TransactionOutput> UTXO;

    public Wallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        final KeyPair keyPair = KeyProvider.generateECKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
        this.UTXO = new HashMap<>();
    }

    public BigDecimal getBalance() {
        return Blockchain.UTXOC.values()
                .stream()
                .filter(output -> output.isMine(publicKey))
                .peek(output -> this.UTXO.put(output.getId(), output))
                .map(TransactionOutput::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Transaction sendFunds(PublicKey recipient, BigDecimal value) throws NoSuchAlgorithmException,
            SignatureException, NoSuchProviderException, InvalidKeyException {

        if (getBalance().compareTo(value) < 0) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        final List<TransactionInput> inputs = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, TransactionOutput> item : UTXO.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total = total.add(UTXO.getValue());
            inputs.add(new TransactionInput(UTXO.getId()));
            if (total.compareTo(value) > 0) {
                break;
            }
        }
        final Transaction transaction = new Transaction(publicKey, recipient, value, inputs);
        transaction.generateSignature(privateKey);

        for (TransactionInput input : inputs) {
            UTXO.remove(input.getTransactionOutputId());
        }
        return transaction;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Map<String, TransactionOutput> getUTXO() {
        return Map.copyOf(UTXO);
    }

    // hashcode and equals

}

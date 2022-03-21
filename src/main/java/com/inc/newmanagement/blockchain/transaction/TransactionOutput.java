package com.inc.newmanagement.blockchain.transaction;

import com.inc.newmanagement.blockchain.util.Hash;
import com.inc.newmanagement.blockchain.util.KeyProvider;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class TransactionOutput {

    private final PublicKey recipient;
    private final BigDecimal value;
    private final String parentTransactionId;
    private final String id;

    public TransactionOutput(PublicKey recipient, BigDecimal value, String parentTransactionId) throws
            NoSuchAlgorithmException {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = Hash.sha256Hex(KeyProvider.getBase64Encoding(recipient) + value + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

    public String getId() {
        return id;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    // hashcode and equals

    @Override
    public String toString() {
        return "TransactionOutput{" +
                "recipient=" + recipient +
                ", value=" + value +
                ", parentTransactionId='" + parentTransactionId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}

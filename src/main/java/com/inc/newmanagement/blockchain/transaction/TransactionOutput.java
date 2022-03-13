package com.inc.newmanagement.blockchain.transaction;

import com.inc.newmanagement.blockchain.util.Hash;
import com.inc.newmanagement.blockchain.util.KeyProvider;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Objects;

public class TransactionOutput {

    private final PublicKey recipientPublicKey;
    private final BigDecimal value;
    private final String parentTransactionId;
    private final String id;

    public TransactionOutput(PublicKey recipientPublicKey, BigDecimal value, String parentTransactionId) throws
            NoSuchAlgorithmException {
        this.recipientPublicKey = recipientPublicKey;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = Hash.sha256Hex(KeyProvider.getBase64Encoding(recipientPublicKey) +
                value + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return publicKey == recipientPublicKey;
    }

    public PublicKey getRecipientPublicKey() {
        return recipientPublicKey;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionOutput that = (TransactionOutput) o;
        return id.equals(that.id) && recipientPublicKey.equals(that.recipientPublicKey) && value.equals(that.value) && parentTransactionId.equals(that.parentTransactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recipientPublicKey, value, parentTransactionId);
    }

    @Override
    public String toString() {
        return "TransactionOutput{" +
                "id='" + id + '\'' +
                ", recipientPublicKey=" + recipientPublicKey +
                ", value=" + value +
                ", parentTransactionId='" + parentTransactionId + '\'' +
                '}';
    }

}

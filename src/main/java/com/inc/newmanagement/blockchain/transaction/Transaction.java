package com.inc.newmanagement.blockchain.transaction;

import com.inc.newmanagement.blockchain.chain.Blockchain;
import com.inc.newmanagement.blockchain.util.Hash;
import com.inc.newmanagement.blockchain.util.KeyProvider;
import com.inc.newmanagement.blockchain.util.Signature;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transaction {

    private byte[] signature;
    private String transactionId;

    private final PublicKey sender;
    private final PublicKey recipient;
    private final BigDecimal value;
    private final List<TransactionInput> inputs;
    private final List<TransactionOutput> outputs;

    private static BigInteger sequence = BigInteger.ZERO;

    public Transaction(PublicKey from, PublicKey to, BigDecimal value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
        this.outputs = new ArrayList<>();
    }

    public void addOutput(TransactionOutput transactionOutput) {
        this.outputs.add(transactionOutput);
    }

    public TransactionOutput getOutput(int index) {
        return this.outputs.get(index);
    }

    public boolean processTransaction() throws NoSuchAlgorithmException,
            SignatureException, NoSuchProviderException, InvalidKeyException {

        if (!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // Gathers transaction inputs (Making sure they are unspent):
        for (TransactionInput input : inputs) {
            final TransactionOutput output = Blockchain.UTXOC.get(input.getTransactionOutputId());
            input.setTransactionOutput(output);
        }

        //Checks if transaction is valid:
        if (getInputsValue().compareTo(Blockchain.MINIMUM_TRANSACTION) < 0) {
            System.out.println("Transaction Inputs too small: " + getInputsValue());
            System.out.println("Please enter the amount greater than " + Blockchain.MINIMUM_TRANSACTION);
            return false;
        }

        //Generate transaction outputs:
        BigDecimal leftOver = getInputsValue().subtract(value); //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //Add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            Blockchain.UTXOC.put(o.getId(), o);
        }

        //Remove transaction inputs from UTXO lists as spent:
        for (TransactionInput input : inputs) {
            if (input.getTransactionOutput() == null) {
                continue;
            }
            Blockchain.UTXOC.remove(input.getTransactionOutput().getId());
        }

        return true;
    }

    public boolean verifySignature() throws NoSuchAlgorithmException, SignatureException,
            NoSuchProviderException, InvalidKeyException {
        final String data = KeyProvider.getBase64Encoding(sender) +
                KeyProvider.getBase64Encoding(recipient) + value;
        return Signature.verifyECDSA(sender, data, signature);
    }

    public void generateSignature(PrivateKey privateKey) throws NoSuchAlgorithmException, SignatureException,
            NoSuchProviderException, InvalidKeyException {
        final String data = KeyProvider.getBase64Encoding(sender) +
                KeyProvider.getBase64Encoding(recipient) + value;
        signature = Signature.applyECDSA(privateKey, data);
    }

    /*
    private byte[] generateSignature(PrivateKey privateKey) throws NoSuchAlgorithmException,
            SignatureException, NoSuchProviderException, InvalidKeyException {
        final String data = KeyProvider.getBase64Encoding(senderPublicKey) +
                KeyProvider.getBase64Encoding(recipientPublicKey) + value;

        return KeyProvider.applyECDSASignature(privateKey, data);
    }*/

    public BigDecimal getOutputsValue() {
        return outputs.stream()
                .map(TransactionOutput::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getInputsValue() {
        // UTXO can be null, use Optional<>?
        return inputs.stream()
                .map(input -> input.getTransactionOutput().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String calculateHash() throws NoSuchAlgorithmException {
        // increase the sequence to avoid two identical transactions having the same hash
        sequence = sequence.add(BigInteger.ONE);
        return Hash.sha256Hex(KeyProvider.getBase64Encoding(sender) +
                KeyProvider.getBase64Encoding(recipient) + value + sequence);
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public BigDecimal getValue() {
        return value;
    }

    public byte[] getSignature() {
        return signature;
    }

    public List<TransactionInput> getInputs() {
        return List.copyOf(inputs);
    }

    public List<TransactionOutput> getOutputs() {
        return List.copyOf(outputs);
    }

    public static BigInteger getSequence() {
        return sequence;
    }

    // Hashcode and equals

    @Override
    public String toString() {
        return "Transaction{" +
                "signature=" + Arrays.toString(signature) +
                ", transactionId='" + transactionId + '\'' +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", value=" + value +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }

}

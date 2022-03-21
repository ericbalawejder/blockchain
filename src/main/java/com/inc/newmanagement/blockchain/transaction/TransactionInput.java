package com.inc.newmanagement.blockchain.transaction;

public class TransactionInput {

    private final String transactionOutputId;
    private TransactionOutput transactionOutput;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    public void setTransactionOutput(TransactionOutput transactionOutput) {
        this.transactionOutput = transactionOutput;
    }

    public String getTransactionOutputId() {
        return transactionOutputId;
    }

    public TransactionOutput getTransactionOutput() {
        return transactionOutput;
    }

    // hashcode and equals

    @Override
    public String toString() {
        return "TransactionInput{" +
                "transactionOutputId='" + transactionOutputId + '\'' +
                ", transactionOutput=" + transactionOutput +
                '}';
    }

}

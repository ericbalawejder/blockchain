package com.inc.newmanagement.blockchain.transaction;

public record TransactionInput(String transactionOutputId, TransactionOutput UTXO) {
}

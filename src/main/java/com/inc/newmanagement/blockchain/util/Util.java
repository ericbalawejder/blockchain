package com.inc.newmanagement.blockchain.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inc.newmanagement.blockchain.transaction.Transaction;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Util {

    private Util() {
        throw new IllegalStateException("utility class");
    }

    public static String getJson(Object o) throws JsonProcessingException {
        return new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(o);
    }

    public static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    public static String getMerkleRoot(List<Transaction> transactions) throws NoSuchAlgorithmException {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }
        List<String> treeLayer = previousTreeLayer;

        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i += 2) {
                treeLayer.add(Hash.sha256Hex(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

}


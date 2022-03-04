package com.inc.newmanagement.blockchain.chain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inc.newmanagement.blockchain.entity.Block;
import com.inc.newmanagement.blockchain.mine.ProofOfWork;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockChain {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        final Block genesisBlock = new Block("genesis block", "0");
        final Block secondBlock = new Block("second block", genesisBlock.getHash());
        final Block thirdBlock = new Block("third block", secondBlock.getHash());

        final List<Block> blockchain = new ArrayList<>(Arrays.asList(genesisBlock, secondBlock, thirdBlock));

        final String output = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(blockchain);

        System.out.println(output);

        System.out.println("Is the chain valid? " + isChainValid(blockchain));

        System.out.println(ProofOfWork.mineBlock(thirdBlock, 5));
    }

    public static Boolean isChainValid(List<Block> blockchain) throws NoSuchAlgorithmException {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }

}

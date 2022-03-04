package com.inc.newmanagement.blockchain.mine;

import com.inc.newmanagement.blockchain.entity.Block;

import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

public class ProofOfWork {

    public static String mineBlock(Block block, int difficulty) throws NoSuchAlgorithmException {
        final String prefixLength = IntStream.range(0, difficulty)
                .mapToObj(i -> '0')
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

        String hash = block.getHash();
        int nonce = block.getNonce();
        while (!hash.substring(0, difficulty).equals(prefixLength)) {
            nonce++;
            hash = new Block(block, nonce).getHash();
        }
        return hash;
    }

}

package com.inc.newmanagement.blockchain.chain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inc.newmanagement.blockchain.entity.Block;
import com.inc.newmanagement.blockchain.transaction.Transaction;
import com.inc.newmanagement.blockchain.transaction.TransactionInput;
import com.inc.newmanagement.blockchain.transaction.TransactionOutput;
import com.inc.newmanagement.blockchain.wallet.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Blockchain {

    private static final int PROVIDER = Security.addProvider(new BouncyCastleProvider());
    private static final int DIFFICULTY = 5;

    public static final BigDecimal MINIMUM_TRANSACTION = BigDecimal.valueOf(0.1);
    public static final List<Block> BLOCKCHAIN = new ArrayList<>();
    public static final Map<String, TransactionOutput> UTXOC = new HashMap<>();
    public static Transaction genesisTransaction;

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            NoSuchProviderException, SignatureException, InvalidKeyException, JsonProcessingException {

        final Wallet walletA = new Wallet();
        final Wallet walletB = new Wallet();
        final Wallet foodCoin = new Wallet();

        genesisTransaction = new Transaction(foodCoin.getPublicKey(), walletA.getPublicKey(),
                new BigDecimal(100), null);

        genesisTransaction.generateSignature(foodCoin.getPrivateKey());
        genesisTransaction.setTransactionId("0");

        final TransactionOutput a = new TransactionOutput(genesisTransaction.getRecipient(),
                genesisTransaction.getValue(),
                genesisTransaction.getTransactionId());

        genesisTransaction.addOutput(a);


        // it is important to store our first transaction in the UTXOs list.
        UTXOC.put(genesisTransaction.getOutput(0).getId(), genesisTransaction.getOutput(0));

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal(40)));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal(1000)));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), new BigDecimal(20)));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println(isChainValid());

        final List<Block> blockchain = new ArrayList<>(Arrays.asList(block1, block2, block3));

        System.out.println(block1);
    }

    public static Boolean isChainValid() throws NoSuchAlgorithmException, SignatureException,
            NoSuchProviderException, InvalidKeyException {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[DIFFICULTY]).replace('\0', '0');
        // a temporary working list of unspent transactions at a given block state.
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.getOutput(0).getId(), genesisTransaction.getOutput(0));

        // loop through blockchain to check hashes:
        for (int i = 1; i < BLOCKCHAIN.size(); i++) {

            currentBlock = BLOCKCHAIN.get(i);
            previousBlock = BLOCKCHAIN.get(i - 1);
            // compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }
            // compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            // check if hash is solved
            if (!currentBlock.getHash().substring(0, DIFFICULTY).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            // loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (!Objects.equals(currentTransaction.getInputsValue(), currentTransaction.getOutputsValue())) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (!Objects.equals(input.getTransactionOutput().getValue(), tempOutput.getValue())) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }
                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for (TransactionOutput output : currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if (currentTransaction.getOutput(0).getRecipient() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.getOutput(1).getRecipient() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        newBlock.mineBlock(DIFFICULTY);
        BLOCKCHAIN.add(newBlock);
    }

}

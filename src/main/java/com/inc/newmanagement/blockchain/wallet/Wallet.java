package com.inc.newmanagement.blockchain.wallet;

import java.security.PrivateKey;
import java.security.PublicKey;

public record Wallet(PrivateKey privateKey, PublicKey publicKey) {
}

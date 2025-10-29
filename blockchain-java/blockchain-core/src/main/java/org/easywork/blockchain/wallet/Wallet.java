package org.easywork.blockchain.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.easywork.blockchain.utils.Utils;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

@Data
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String blockchainAddress;

    public Wallet() {
        generateKeyPair();
        blockchainAddress = Utils.generateBlockchainAddress(publicKey);
        print();
    }

    public Transaction newTransaction(String publicKeyStr, String privateKeyStr, String sender, String recipient, Float value) {
        return new Transaction(Utils.generatePublicKey(publicKeyStr), Utils.generatePrivateKey(privateKeyStr), sender, recipient, value);
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1"); // P-256 椭圆曲线
            keyGen.initialize(ecSpec, new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void print() {
        System.err.println("Wallet Address: " + blockchainAddress);
        System.err.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.err.println("Private Key: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    }

    @AllArgsConstructor
    @Data
    public static class Transaction {
        private PublicKey senderPublicKey;
        private PrivateKey senderPrivateKey;
        private String senderBlockchainAddress;
        private String recipientBlockchainAddress;
        private Float value;

        public String generateSignature() {
            String data = senderBlockchainAddress + recipientBlockchainAddress + value;
            return Utils.sign(senderPrivateKey, data);
        }
    }

    @Data
    public static class TransactionRequest {
        private String senderBlockchainAddress;
        private String recipientBlockchainAddress;
        private String senderPublicKey;
        private String senderPrivateKey;
        private Float value;

        public boolean validate() {
            return senderBlockchainAddress != null &&
                    recipientBlockchainAddress != null &&
                    senderPublicKey != null &&
                    senderPrivateKey != null &&
                    value != null;
        }

    }
}
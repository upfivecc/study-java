package org.easywork.blockchain;

import lombok.Data;

import java.security.MessageDigest;
import java.util.List;

@Data
public class Block {
    private long timestamp;
    private int nonce;
    private String previousHash;
    private List<Transaction> transactions;

    public Block(int nonce, String previousHash, List<Transaction> transactions) {
        this.timestamp = System.currentTimeMillis();
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.transactions = transactions;
    }

    public String hash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = timestamp + previousHash + nonce + transactions.toString();
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void print() {
        System.out.println("timestamp: " + timestamp);
        System.out.println("nonce: " + nonce);
        System.out.println("previousHash: " + previousHash);
        transactions.forEach(Transaction::print);
    }
}
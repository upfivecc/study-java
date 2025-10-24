package org.easywork.blockchain;

import lombok.Data;

import java.security.PublicKey;

@Data
public class Transaction {
    private final String sender;
    private final String recipient;
    private final float value;
    private String signature; // 🆕 增加签名字段

    public Transaction(String sender, String recipient, float value) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
    }

    public void signTransaction(Wallet wallet) {
        String data = sender + recipient + value;
        this.signature = wallet.sign(data);
    }

    public boolean isValid(PublicKey publicKey) {
        String data = sender + recipient + value;
        return Wallet.verify(data, signature, publicKey);
    }

    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public float getValue() { return value; }
    public String getSignature() { return signature; }

    public void print() {
        System.out.println("Sender: " + sender);
        System.out.println("Recipient: " + recipient);
        System.out.println("Value: " + value);
        System.out.println("Signature: " + signature);
        System.out.println("----------------------------------");
    }

    @Override
    public String toString() {
        return sender + recipient + value + (signature != null ? signature : "");
    }
}
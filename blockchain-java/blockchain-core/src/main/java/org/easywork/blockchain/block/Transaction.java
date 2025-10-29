package org.easywork.blockchain.block;

import lombok.Data;
import org.easywork.blockchain.utils.Utils;
import org.easywork.blockchain.wallet.Wallet;

import java.security.PublicKey;

@Data
public class Transaction {
    private final String sender;
    private final String recipient;
    private final float value;
    // 增加签名字段
    private String signature;

    public Transaction(String sender, String recipient, float value, String signature) {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.signature = signature;
    }

    public boolean isValid(PublicKey publicKey) {
        String data = sender + recipient + value;
        return Utils.verify(data, signature, publicKey);
    }

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
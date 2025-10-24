package org.easywork.blockchain;

import lombok.Data;

@Data
public class TransactionRequest {
    private String senderBlockchainAddress;
    private String recipientBlockchainAddress;
    private String senderPublicKey;
    private Float value;
    private String signature;

    public boolean validate() {
        return senderBlockchainAddress != null &&
               recipientBlockchainAddress != null &&
               senderPublicKey != null &&
               value != null &&
               signature != null;
    }
}
package org.easywork.blockchain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/10/28 09:59
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final BlockchainProxy blockchain;
    private final Wallet wallet = new Wallet();

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 新建钱包
     *
     * @return
     */
    @GetMapping("/newWallet")
    @ResponseBody
    public Map<String, Object> newWallet() {
        return Map.of("publicKey", Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded()), "privateKey"
                , Base64.getEncoder().encodeToString(wallet.getPrivateKey().getEncoded()), "blockchainAddress", wallet.getBlockchainAddress());
    }

    /**
     * 查看余额
     *
     * @param address
     * @return
     */
    @GetMapping("/balance/{address}")
    @ResponseBody
    public Map<String, Object> getBalance(@PathVariable(name = "address") String address) {
        return blockchain.calculateBalance(address);
    }

    /**
     * 交易接口
     *
     * @param txRequest
     * @return
     */
    @PostMapping("/transaction")
    @ResponseBody
    public Map<String, Object> transaction(@RequestBody Wallet.TransactionRequest txRequest) {
        // 确保交易请求包含所有必要信息
        if (!txRequest.validate()) {
            return Map.of("error", "Missing required transaction fields");
        }

        Transaction transaction = wallet.newTransaction(txRequest.getSenderPublicKey(), txRequest.getSenderPrivateKey()
                , txRequest.getSenderBlockchainAddress(), txRequest.getRecipientBlockchainAddress(), txRequest.getValue());

        Blockchain.TransactionRequest transactionRequest = new Blockchain.TransactionRequest();
        transactionRequest.setSenderPublicKey(txRequest.getSenderPublicKey());
        transactionRequest.setSenderPrivateKey(txRequest.getSenderPrivateKey());
        transactionRequest.setValue(txRequest.getValue());
        transactionRequest.setSignature(transaction.getSignature());
        transactionRequest.setSenderBlockchainAddress(txRequest.getSenderBlockchainAddress());
        transactionRequest.setRecipientBlockchainAddress(txRequest.getRecipientBlockchainAddress());

        return blockchain.createTransaction(transactionRequest);
    }

}
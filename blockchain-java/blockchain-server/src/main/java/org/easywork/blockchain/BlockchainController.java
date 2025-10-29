package org.easywork.blockchain;

import lombok.RequiredArgsConstructor;
import org.easywork.blockchain.block.Blockchain;
import org.easywork.blockchain.block.Transaction;
import org.easywork.blockchain.utils.Utils;
import org.easywork.blockchain.wallet.Wallet;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/10/28 09:50
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/blockchain")
public class BlockchainController {

    private final Blockchain blockchain;

    private final Wallet minerWallet;
    private final Map<String, Blockchain> cache = new HashMap<>();

    /**
     * 查看全链
     *
     * @return
     */
    @GetMapping("/getBlockchain")
    public Blockchain getBlockChain() {
        return blockchain;
    }

    /**
     * 计算余额
     *
     * @return
     */
    @GetMapping("/calculateBalance/{address}")
    public Map<String, Object> calculateBalance(@PathVariable(name = "address") String address) {
        float balance = blockchain.calculateBalance(address);
        return Map.of("balance", balance);
    }


    /**
     * 添加交易（已签名）
     *
     * @param txRequest
     * @return
     */
    @PostMapping("/createTransaction")
    public Map<String, Object> createTransaction(@RequestBody Blockchain.TransactionRequest txRequest) {
        if (!txRequest.validate()) {
            return Map.of("error", "Invalid transaction data");
        }

        // 构造交易对象
        Transaction tx = new Transaction(
                txRequest.getSenderBlockchainAddress(),
                txRequest.getRecipientBlockchainAddress(),
                txRequest.getValue()
        );

        PublicKey senderPublicKey = Utils.generatePublicKey(txRequest.getSenderPublicKey());
        // 添加交易到区块链
        boolean added = blockchain.createTransaction(tx, senderPublicKey);
        return Map.of("success", added);
    }

    /**
     * 手动挖矿
     *
     * @return
     */
    @GetMapping("/mine")
    public Map<String, Object> mine() {
        blockchain.mine();
        return Map.of("status", "mined", "miner_address", minerWallet.getBlockchainAddress());
    }

    /**
     * 自动挖矿
     *
     * @return
     */
    @GetMapping("/startMine")
    public Map<String, Object> startMine() {
        blockchain.startMining();
        return Map.of("status", "mining", "miner_address", minerWallet.getBlockchainAddress());
    }

    @GetMapping("/consensus")
    public Map<String, Object> consensus() {
        this.blockchain.resolveConflicts();
        return Map.of("status", "success");
    }

    /**
     * 查看节点信息
     *
     * @return
     */
    @GetMapping("/nodeInfo")
    public Map<String, Object> nodeInfo() {
        return Map.of(
                "miner_address", minerWallet.getBlockchainAddress(),
                "chain_length", blockchain.getChain().size()
        );
    }


}

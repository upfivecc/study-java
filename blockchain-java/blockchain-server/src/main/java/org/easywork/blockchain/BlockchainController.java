package org.easywork.blockchain;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/addTransaction")
    public Map<String, Object> addTransaction(@RequestBody TransactionRequest txRequest) {
        if (!txRequest.validate()) {
            return Map.of("error", "Invalid transaction data");
        }

        // 构造交易对象
        Transaction tx = new Transaction(
                txRequest.getSenderBlockchainAddress(),
                txRequest.getRecipientBlockchainAddress(),
                txRequest.getValue()
        );
        tx.signTransaction(minerWallet);

        // 这里只是演示，实际应从公钥反序列化
        boolean added = blockchain.createTransaction(tx, minerWallet.getPublicKey());
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

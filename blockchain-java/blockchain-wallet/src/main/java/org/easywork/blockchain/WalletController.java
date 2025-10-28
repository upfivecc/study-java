package org.easywork.blockchain;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/10/28 09:59
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final BlockchainProxy blockchain;

    /**
     * 新建钱包
     *
     * @return
     */
    @GetMapping("/newWallet")
    public Map<String, Object> newWallet() {
        Wallet wallet = new Wallet();
        return Map.of("publicKey", Base64.getEncoder().encodeToString(wallet.getPublicKey().getEncoded()), "privateKey"
                , Base64.getEncoder().encodeToString(wallet.getPrivateKey().getEncoded()), "address", wallet.getBlockchainAddress());
    }

    /**
     * 查看余额
     *
     * @param address
     * @return
     */
    @GetMapping("/balance/{address}")
    public Map<String, Object> getBalance(@PathVariable(name = "address") String address) {
        return blockchain.calculateBalance(address);
    }

    /**
     * 交易接口
     *
     * @param txRequest
     * @return
     */
    @GetMapping("/transaction")
    public Map<String, Object> transaction(@RequestBody TransactionRequest txRequest) {
        return blockchain.addTransaction(txRequest);
    }

}

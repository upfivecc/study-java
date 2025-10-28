package org.easywork.blockchain;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/10/28 10:04
 */
@Service
public class BlockchainProxy {

    @Value("${blockchain.gateway:http://localhost:5000}")
    private String gateway;

    @Resource
    private RestTemplate restTemplate;

    public Map<String, Object> calculateBalance(String address) {
        return restTemplate.getForObject(gateway + "/blockchain/calculateBalance/" + address, Map.class);
    }

    public Map<String, Object> addTransaction(TransactionRequest txRequest) {
        return restTemplate.postForObject(gateway + "/blockchain/addTransaction", txRequest, Map.class);
    }
}

package org.easywork.blockchain;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/10/24 18:29
 */
@SpringBootApplication
public class BlockchainServer {

    @Value("${server.port:5000}")
    private int port;
    public static void main(String[] args) {
        SpringApplication.run(BlockchainServer.class, args);
    }

    @Bean
    public Blockchain blockchain(Wallet minerWallet) {
        return new Blockchain(minerWallet.getBlockchainAddress(), port);
    }

    @Bean
    public Wallet minerWallet() {
        return new Wallet();
    }

}

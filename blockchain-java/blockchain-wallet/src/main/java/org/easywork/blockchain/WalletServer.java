package org.easywork.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/10/24 18:29
 */
@SpringBootApplication
public class WalletServer {
    public static void main(String[] args) {
        String port = args[0];
        String gateway = args[1];
        SpringApplication.run(WalletServer.class, args);
    }
}

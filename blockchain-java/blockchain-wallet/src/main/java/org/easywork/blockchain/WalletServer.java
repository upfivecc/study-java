package org.easywork.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/10/24 18:29
 */
@SpringBootApplication
public class WalletServer {

    public static void main(String[] args) {
        SpringApplication.run(WalletServer.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时 5s
        factory.setConnectTimeout(5000);
        // 读取超时 5s
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }
}

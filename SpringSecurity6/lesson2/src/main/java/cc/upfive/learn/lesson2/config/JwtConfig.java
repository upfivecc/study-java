package cc.upfive.learn.lesson2.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Configuration
public class JwtConfig {

    /**
     * 至少 256 位
     */
    private static final String SECRET = "AuZUVAVty5cvLftEoTkgkP435i4TsaXuWEy/iBYr9PE=";

    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] secretBytes = Base64.getDecoder().decode(SECRET);
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");

        // 构造 OctetSequenceKey，指定 alg 和 use
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey)
                .algorithm(JWSAlgorithm.HS256) // 指定 HS256
                .keyID("hs256-key")            // 可选，但最好加
                .build();

        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = Base64.getDecoder().decode(SECRET);
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
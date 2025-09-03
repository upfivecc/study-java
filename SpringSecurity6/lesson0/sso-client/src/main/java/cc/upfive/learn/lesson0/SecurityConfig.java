package cc.upfive.learn.lesson0;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/login/oauth2/code/**").permitAll()
                .anyRequest().authenticated()
            )
//            .oauth2Login(oauth2 -> oauth2
//                .loginPage("/oauth2/authorization/demo-client") // 跳转授权服务器
//            )
                .oauth2Login(Customizer.withDefaults())
        ;
        return http.build();
    }
}
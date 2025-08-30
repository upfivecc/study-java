package cc.upfive.learn.lesson2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 适合：接入 Google、GitHub、微信 等 OAuth2 登录。
 */
//@EnableWebSecurity
//@Configuration
public class OAuth2SecurityConfig {

    @Bean
    public SecurityFilterChain oauth2LoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // 自定义登录页
                        .loginPage("/login")
                        // 登录成功跳转
                        .defaultSuccessUrl("/home")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
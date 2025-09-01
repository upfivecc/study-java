package cc.upfive.learn.lesson2.config;

import cc.upfive.learn.lesson2.util.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * 适合：移动端、小程序、前端 SPA，不依赖 session，token 校验。
 *
 */
@EnableWebSecurity
@Configuration
public class RestSecurityConfig {

    private final JwtTokenService jwtTokenService;

    public RestSecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }


    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
        http
                // REST 场景禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 无 session
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 登录、注册接口放行
                        .requestMatchers("/api/auth/login", "/api/auth/loginFilter").permitAll()
                        .anyRequest().authenticated()
                )
        // 假设使用 JWT 作为资源服务器，主要是解析 header中的 token
        // 方法一：使用 oauth2ResourceServer的 token拦截器
        //.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        // 方法二： 自定义拦截器
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
        // 方法三：实现 AuthenticationFilter，不建议
        //.addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class)

        ;

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails apiUser = User.withUsername("user")
                .password("{noop}123456")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(apiUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager, JsonAuthenticationConverter jsonAuthenticationConverter) {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, jsonAuthenticationConverter);
        authenticationFilter.setRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/auth/loginFilter"));
        authenticationFilter.setSuccessHandler((request, response, authentication) -> {
            String token = jwtTokenService.generateToken(authentication);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"token\":\"" + token + "\"}");
            response.getWriter().flush();
        });
        authenticationFilter.setFailureHandler((request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"" + exception.getMessage() + "\"}");
            //response.getWriter().flush();
        });

        return authenticationFilter;
    }
}
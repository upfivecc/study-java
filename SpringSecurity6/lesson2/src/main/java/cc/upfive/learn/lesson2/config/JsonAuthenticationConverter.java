package cc.upfive.learn.lesson2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author: fiveupup
 * @version: 1.0.0
 * @date: 2025/9/1 11:14
 */
@Component
public class JsonAuthenticationConverter implements AuthenticationConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod())) {
            return null;
        }
        try {
            Map loginData = objectMapper.readValue(request.getInputStream(), Map.class);
            String username = (String) loginData.get("username");
            String password = (String) loginData.get("password");
            return new UsernamePasswordAuthenticationToken(username, password);
        } catch (IOException e) {
            return null;
        }
    }
}

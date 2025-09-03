package cc.upfive.learn.lesson0;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ClientController {

    @GetMapping("/index")
    public String index() {
        // 首页
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        // 未登录页
        return "login";
    }

    @GetMapping("/user")
    public String user(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        System.out.println(oidcUser);
        model.addAttribute("user", oidcUser);
        return "user"; // 登录后展示用户信息
    }
}
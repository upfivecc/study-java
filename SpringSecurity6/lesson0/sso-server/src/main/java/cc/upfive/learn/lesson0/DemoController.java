package cc.upfive.learn.lesson0;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/home")
    public String home() {
        return "home"; // templates/home.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }
}
package cc.upfive.learn.lesson2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/8/20 16:29
 */
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}

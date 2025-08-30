package cc.upfive.learn.lesson2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: upfive
 * @version: 1.0.0
 * @date: 2025/8/20 16:29
 */
@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "welcome to spring security home";
    }
}

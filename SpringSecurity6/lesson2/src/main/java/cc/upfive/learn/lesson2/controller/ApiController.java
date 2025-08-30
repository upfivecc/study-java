package cc.upfive.learn.lesson2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fiveupup
 * @version: 1.0.0
 * @date: 2025/8/29 15:36
 */
@RestController
public class ApiController {

    @GetMapping("/api/hello")
    public String api() {
        return "hello api";
    }
}

package com.example.cicddemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/helloV4")
    public String helloV4() {
        return "hello world V4";
    }

    @GetMapping("/helloV3")
    public String helloV3() {
        return "hello world V3";
    }
}

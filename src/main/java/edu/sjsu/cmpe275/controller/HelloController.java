package edu.sjsu.cmpe275.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "hello";
    }

    @RequestMapping("/login")
    public String loginView() {
        return "hello";
    }

}

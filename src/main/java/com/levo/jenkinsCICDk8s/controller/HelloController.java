package com.levo.dockerexample.controller;

import java.util.Date;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String test() {
        return "hello! This is a test app." + \n "Congratulations on setting up docker image successfully."\n +
            "/\\_/\\  \n" \n +
            "( o.o ) \n" \n +
            " > ^ < ";
    }
}

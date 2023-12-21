package com.example.website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShowRatePage {
    @GetMapping("/rate")
    public String showRate() {
        return "rate";
    }
}

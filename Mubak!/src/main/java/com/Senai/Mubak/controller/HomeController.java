package com.Senai.Mubak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // A página inicial é pública e apenas encaminha para o template home.html.
    @GetMapping("/")
    public String inicio() {
        return "home";
    }
}
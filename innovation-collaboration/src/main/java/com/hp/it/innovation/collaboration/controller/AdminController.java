package com.hp.it.innovation.collaboration.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/*")
public class AdminController {

    @RequestMapping
    public String processAdmin(Model model, HttpServletRequest request) {
        return "";
    }
    
}

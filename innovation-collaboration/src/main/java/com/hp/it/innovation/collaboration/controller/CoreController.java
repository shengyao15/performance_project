package com.hp.it.innovation.collaboration.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CoreController {
    
    public static String HOME_PAGE_URL = "index";
    
    @RequestMapping("/")
    public String processHome(Model model, HttpServletRequest request){
        return HOME_PAGE_URL; 
    }
}

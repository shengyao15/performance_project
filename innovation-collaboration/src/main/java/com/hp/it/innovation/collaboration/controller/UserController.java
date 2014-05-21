package com.hp.it.innovation.collaboration.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.model.DashboardPanel;
import com.hp.it.innovation.collaboration.service.common.ServiceFactory;
import com.hp.it.innovation.collaboration.service.intf.UserService;

@Controller
@RequestMapping("/users/*")
public class UserController {

    @RequestMapping("/{userName}")
    public String processUser(Model model, HttpServletRequest request, @PathVariable String userName) {
        List<UserDTO> users = ServiceFactory.getService(UserService.class).retrieveAllUsers();

        // setup the panel list
        List<DashboardPanel> panels = new ArrayList<DashboardPanel>();
        panels.add(new DashboardPanel("my profile", "myprofile", null, null));
        panels.add(new DashboardPanel("todo reminder", "todoreminder", null, null));
        panels.add(new DashboardPanel("ad1", "ad1", null, null));
        panels.add(new DashboardPanel("skill matrix", "skillmatrix", null, null));
        panels.add(new DashboardPanel("ad2", "ad2", null, null));
        panels.add(new DashboardPanel("ad3", "ad3", null, null));
        panels.add(new DashboardPanel("Placeholder1", "placeholder1", null, null));
        panels.add(new DashboardPanel("Placeholder2", "placeholder2", null, null));
        panels.add(new DashboardPanel("Admin", "admin", null, null));
        model.addAttribute("panels", panels);
        System.out.println("panel count : " + panels.size());

        System.out.println(users.get(0).getDisplayName());
        System.out.println(userName);
        return "user/dashboard";
    }

}

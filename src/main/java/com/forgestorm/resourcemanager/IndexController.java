package com.forgestorm.resourcemanager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class IndexController {

    @RequestMapping("/index")
    public String showLoginForm() {
        return "index";
    }

    @PostMapping("/index")
    public String login(@RequestParam String username, @RequestParam String password) {
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        return "redirect:/dashboard";
    }
}

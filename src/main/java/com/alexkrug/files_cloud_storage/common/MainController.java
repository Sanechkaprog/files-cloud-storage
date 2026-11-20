package com.alexkrug.files_cloud_storage.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }

    @RequestMapping(value = "/files/**")
    public String files() {
        return "redirect:/login";
    }
}
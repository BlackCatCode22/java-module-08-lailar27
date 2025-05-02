package com.example.Restful.API;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.service.annotation.GetExchange;

@Controller
public class WebController {

    /**
     * Maps the root URL ("/") to the index.html file in the static folder.
     *
     * @return the name of the index.html.file
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
}

package com.pocketskilltrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    /**
     * Forwards non-API client routes to index.html to allow React Router to handle page routing.
     */
    @GetMapping(value = {
        "/dashboard/**",
        "/courses/**",
        "/quizzes/**",
        "/quiz/**",
        "/results/**",
        "/predictions/**",
        "/pdf-summarizer/**",
        "/achievements/**",
        "/profile/**",
        "/admin/**",
        "/login",
        "/register"
    })
    public String forward() {
        return "forward:/index.html";
    }
}

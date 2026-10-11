package com.pocketskilltrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class PocketSkillTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocketSkillTrackApplication.class, args);
    }
}

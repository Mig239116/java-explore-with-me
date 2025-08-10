package ru.practicum.mainsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.mainsvc",
        "ru.practicum.statsclient"
})
public class MainSVCApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainSVCApplication.class, args);
    }
}
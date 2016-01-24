package ua.djhans.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.djhans.controller.SparkController;

/**
 * Main класс для запуска приложения
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SparkController.getInstance();
        SpringApplication.run(Application.class, args);
    }
}

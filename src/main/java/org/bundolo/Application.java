package org.bundolo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

//@ComponentScan
@EnableAutoConfiguration
// @Configuration
@ImportResource("/applicationContext.xml")
public class Application {

    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }
}

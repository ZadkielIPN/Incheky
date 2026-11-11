package org.handel.incheki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InchekyApplication {

    static void main(String[] args) {
        SpringApplication.run(InchekyApplication.class, args);
    }

}

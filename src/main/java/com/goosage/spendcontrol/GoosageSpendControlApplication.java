package com.goosage.spendcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.goosage.infra.repository")
@EntityScan(basePackages = "com.goosage")
public class GoosageSpendControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoosageSpendControlApplication.class, args);
    }
}

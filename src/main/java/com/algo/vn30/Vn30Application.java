package com.algo.vn30;

import com.algo.vn30.worker.BaseWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.jms.annotation.EnableJms;

import java.util.Arrays;


@SpringBootApplication
@EntityScan("com.algo.vn30.*")
@EnableTransactionManagement
@EnableJpaRepositories({"com.algo.vn30.persistence"})
@EnableScheduling
public class Vn30Application implements CommandLineRunner  {

    public static void main(String[] args) {
        SpringApplication.run(Vn30Application.class, args);
    }

    @Autowired
    BaseWorker worker;

    @Override
    public void run(String... args) throws Exception {
        try {
            worker.start("pl");
        } catch (Exception e) {
            try {
            } catch (Exception ignored) {
            }
        }
    }
}

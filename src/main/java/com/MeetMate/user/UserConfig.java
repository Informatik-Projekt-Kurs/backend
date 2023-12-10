package com.MeetMate.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository repository) {
        return args -> {
            User carl = new User(
                    "Carl A",
                    LocalDate.of(2000, Month.JANUARY, 21),
                    "carl.A@gmail.com",
                    "carl123"
            );

            User karlotta = new User(
                    "Karlotta B",
                    LocalDate.of(2006, Month.OCTOBER, 14),
                    "karlotta.b@gmail.com",
                    "karlotta123"
            );

            repository.saveAll(
                    List.of(carl, karlotta));
        };
    }
}

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
                    List.of("Carl", "A"),
                    LocalDate.of(2000, Month.JANUARY, 21),
                    "carl.A@gmail.com"
            );

            User hannah = new User(
                    List.of("Karlotta", "B"),
                    LocalDate.of(2006, Month.OCTOBER, 14),
                    "karlotta.b@gmail.com"
            );

            repository.saveAll(
                    List.of(carl, hannah));
        };
    }
}

package com.MeetMate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableConfigurationProperties
// @EntityScan(basePackages = {"com.MeetMate.user"}) //force scan the packages
public class MeetMateApplication {

  public static void main(String[] args) {
    SpringApplication.run(MeetMateApplication.class, args);
  }
}

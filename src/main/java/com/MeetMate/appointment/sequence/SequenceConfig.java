package com.MeetMate.appointment.sequence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SequenceConfig {

  @Bean
  public CommandLineRunner init(MongoTemplate mongoTemplate) {
    return args -> {
      if (!mongoTemplate.collectionExists(AppointmentSequence.class)) {
        mongoTemplate.createCollection(AppointmentSequence.class);
        mongoTemplate.insert(new AppointmentSequence("appointment_sequence", 0));
      }
    };
  }
}

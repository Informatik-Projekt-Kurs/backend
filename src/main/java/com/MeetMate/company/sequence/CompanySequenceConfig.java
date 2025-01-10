package com.MeetMate.company.sequence;

import com.MeetMate.appointment.sequence.AppointmentSequence;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class CompanySequenceConfig {

  @Bean
  public CommandLineRunner init(MongoTemplate mongoTemplate) {
    return args -> {
      if (!mongoTemplate.collectionExists(CompanySequence.class)) {
        mongoTemplate.createCollection(CompanySequence.class);
        mongoTemplate.insert(new CompanySequence("company_sequence", 0));
      }
      if (!mongoTemplate.collectionExists(AppointmentSequence.class)) {
        mongoTemplate.createCollection(AppointmentSequence.class);
        mongoTemplate.insert(new AppointmentSequence("appointment_sequence", 0));
      }
    };
  }
}

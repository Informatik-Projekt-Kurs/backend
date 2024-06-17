package com.MeetMate.company.sequence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SequenceConfig {

  @Bean
  public CommandLineRunner init(MongoTemplate mongoTemplate) {
    return args -> {
      if (!mongoTemplate.collectionExists(CompanySequence.class)) {
        mongoTemplate.createCollection(CompanySequence.class);
        mongoTemplate.insert(new CompanySequence("company_sequence", 0));
      }
    };
  }
}

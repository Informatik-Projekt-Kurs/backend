package com.MeetMate.appointment.sequence;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentSequenceService {

  private final MongoTemplate mongoTemplate;

  @Transactional
  public void incrementId() {
    Query query = new Query(Criteria.where("_id").is("appointment_sequence"));
    Update update = new Update().inc("value", 1);
    mongoTemplate.updateFirst(query, update, AppointmentSequence.class);
  }

  public long getCurrentValue(){
    Query query = new Query(Criteria.where("_id").is("appointment_sequence"));
    AppointmentSequence sequence = mongoTemplate.findOne(query, AppointmentSequence.class);
    return sequence.getValue();
  }


}

package com.MeetMate.company.sequence;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceService {

  private final MongoTemplate mongoTemplate;

  @Transactional
  public void incrementId() {
    Query query = new Query(Criteria.where("_id").is("company_sequence"));
    Update update = new Update().inc("value", 1);
    mongoTemplate.updateFirst(query, update, CompanySequence.class);
  }

  public long getAndIncrementCurrentValue(){
    Query query = new Query(Criteria.where("_id").is("company_sequence"));
    CompanySequence sequence = mongoTemplate.findOne(query, CompanySequence.class);
    long value = sequence.getValue();
    incrementId();
    return value;
  }


}

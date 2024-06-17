package com.MeetMate.company.sequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "company_sequence")
@Data
@AllArgsConstructor
public class CompanySequence {
  @Id
  String id;
  long value;

}
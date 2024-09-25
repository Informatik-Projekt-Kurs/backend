package com.MeetMate.appointment.sequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appointment_sequence")
@Data
@AllArgsConstructor
public class AppointmentSequence {
  @Id
  String id;
  long value;

}
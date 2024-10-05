package com.MeetMate.appointment;

import com.MeetMate.enums.AppointmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
public class Appointment {
  long id;
  LocalDateTime from;
  LocalDateTime to;
  long companyID;
  long clientID;
  long assigneeID;
//  Select Prompt → f.E. medical industry: Untersuchung, Operation
  String description;
  String location;
  AppointmentStatus status;

  public Appointment(long id, long companyID, long clientID) {
    this.id = id;
    this.companyID = companyID;
    this.clientID = clientID;
  }
}

package com.MeetMate.appointment;

import com.MeetMate.enums.AppointmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
public class Appointment {
  private long id;
  private String from;
  private String to;
  private long companyID;
  private long clientID;
  private long assigneeID;
  //  Select Prompt → f.E. medical industry: Untersuchung, Operation
  private String description;
  private String location;
  private AppointmentStatus status;

  public Appointment(long id, long companyID) {
    this.id = id;
    this.companyID = companyID;
  }
}

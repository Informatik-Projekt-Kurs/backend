package com.MeetMate.appointment;

import com.MeetMate.enums.AppointmentStatus;
import lombok.Builder;
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
  private long companyId;
  private long clientId;
  private long assigneeId;
  //  Select Prompt → f.E. medical industry: Untersuchung, Operation
  private String description;
  private String location;
  private AppointmentStatus status;

  public Appointment(long id, long companyId) {
    this.id = id;
    this.companyId = companyId;
  }
}

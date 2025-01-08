package com.MeetMate.appointment;

import com.MeetMate.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
public class Appointment {
  private long id;
  private Instant from;
  private Instant to;
  private long companyId;
  private long clientId;
  private String description;
  private String location;
  private AppointmentStatus status;

  public Appointment(long id, long companyId) {
    this.id = id;
    this.companyId = companyId;
  }
}

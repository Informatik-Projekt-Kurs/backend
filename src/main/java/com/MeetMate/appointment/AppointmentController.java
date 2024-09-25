package com.MeetMate.appointment;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InaccessibleObjectException;
import java.util.Map;

@Controller
@RequestMapping(path = "api/appointment")
@RequiredArgsConstructor
public class AppointmentController {
  private final AppointmentService appointmentService;

  @QueryMapping
  public Appointment getAppointment(@Argument long id) {
    try {
      return appointmentService.getAppointment(id);

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      return null;
//            if (tc == EntityNotFoundException.class)
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());
//
//            if (tc == IllegalArgumentException.class)
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> createAppointment(
      @Argument String from,
      @Argument String to,
      @Argument long companyId,
      @Argument long clientId,
      @Argument long assigneeId,
//      @Argument Select Prompt → f.E. medical industry: Untersuchung, Operation,
      @Argument String description,
      @Argument String location,
      @Argument String status) {
    try {
      Map<String, Object> appointmentData = Map.of(
          "from", from,
          "to", to,
          "assigneeId", assigneeId,
          //  "prompt", Select Prompt → f.E. medical industry: Untersuchung, Operation);
          "description", description,
          "location", location,
          "status", status
      );

      appointmentService.createAppointment(companyId, clientId, appointmentData);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      if (tc == InaccessibleObjectException.class)
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

}

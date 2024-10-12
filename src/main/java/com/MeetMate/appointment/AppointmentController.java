package com.MeetMate.appointment;

import com.MeetMate.enums.AppointmentStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path = "api/appointment")
@RequiredArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;

  @QueryMapping
  public Appointment getAppointment(
      @ContextValue String token,
      @Argument long id
  ) {
    try {
      return appointmentService.getAppointment(token, id);

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
      @Argument String status
  ) {
    try {
      appointmentService.createAppointment(from, to, companyId, clientId, assigneeId, description, location, AppointmentStatus.valueOf(status));
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> editAppointment(
      @ContextValue String token,
      @Argument long id,
      @Argument String from,
      @Argument String to,
      @Argument long clientId,
      @Argument long assigneeId,
//      @Argument Select Prompt → f.E. medical industry: Untersuchung, Operation,
      @Argument String description,
      @Argument String location,
      @Argument String status) {

    try {
      appointmentService.editAppointment(token, id, from, to, clientId, assigneeId, description, location, AppointmentStatus.valueOf(status));
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      if (tc == IllegalStateException.class)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

      if (tc == IllegalArgumentException.class)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> deleteAppointment(
      @ContextValue String token,
      @Argument long id) {

    try {
      appointmentService.deleteAppointment(token, id);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == IllegalStateException.class)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

      if (tc == IllegalArgumentException.class)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

}

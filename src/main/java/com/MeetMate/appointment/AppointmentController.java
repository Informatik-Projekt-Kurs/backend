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

import java.time.Instant;

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
    token = token.substring(7);
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
  public ResponseEntity<?> bookAppointment(
      @ContextValue String token,
      @Argument long appointmentId) {
    token = token.substring(7);
    try {
      appointmentService.bookAppointment(token, appointmentId);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      if (tc == IllegalAccessException.class)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> createAppointment(
      @ContextValue String token,
      @Argument Instant from,
      @Argument Instant to,
      @Argument long clientId,
//      @Argument Select Prompt → f.E. medical industry: Untersuchung, Operation,
      @Argument String title,
      @Argument String description,
      @Argument String location
  ) {
    token = token.substring(7);
    try {
      appointmentService.createAppointment(token, from, to, clientId, title, description, location);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      if (tc == IllegalArgumentException.class)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> editAppointment(
      @ContextValue String token,
      @Argument long id,
      @Argument Instant from,
      @Argument Instant to,
      @Argument long clientId,
//      @Argument Select Prompt → f.E. medical industry: Untersuchung, Operation,
      @Argument String title,
      @Argument String description,
      @Argument String location,
      @Argument AppointmentStatus status
  ) {
    token = token.substring(7);
    try {
      appointmentService.editAppointment(token, id, from, to, clientId, title, description, location, status);
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
      @Argument long id
  ) {
    token = token.substring(7);
    try {
      appointmentService.deleteAppointment(token, id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

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

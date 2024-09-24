package com.MeetMate.appointment;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

}

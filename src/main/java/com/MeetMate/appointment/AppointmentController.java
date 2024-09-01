//package com.MeetMate.appointment;
//
//import com.MeetMate.company.Company;
//import lombok.RequiredArgsConstructor;
//import org.springframework.graphql.data.method.annotation.Argument;
//import org.springframework.graphql.data.method.annotation.QueryMapping;
//
//@RequiredArgsConstructor
//public class AppointmentController {
//  private final AppointmentService appointmentService;
//
//  @QueryMapping
//  public Company getAppointment(@Argument long id) {
//    try {
//      return appointmentService.getAppointment(id);
//
//    } catch (Throwable t) {
//      Class<? extends Throwable> tc = t.getClass();
//      return null;
////            if (tc == EntityNotFoundException.class)
////                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());
////
////            if (tc == IllegalArgumentException.class)
////                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());
////
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
//    }
//  }
//
//}

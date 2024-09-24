//package com.MeetMate.appointment;
//
//import com.MeetMate.company.CompanyRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AppointmentConfig {
//
//  @Bean
//  public CommandLineRunner run(AppointmentRepository appointmentRepository) throws Exception {
//  System.out.println("Yeehaaaa");
//    return args -> {
//      Appointment appointment = new Appointment();
//      appointment.setId(1L);
//      appointment.setCompanyID(1L);
//
//      appointmentRepository.save(appointment);
//    };
//  }
//
//
//}

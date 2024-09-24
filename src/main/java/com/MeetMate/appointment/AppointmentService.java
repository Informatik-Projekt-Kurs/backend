package com.MeetMate.appointment;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;

  public Appointment getAppointment(long id) {
    return appointmentRepository.findAppointmentById(id)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
  }
}

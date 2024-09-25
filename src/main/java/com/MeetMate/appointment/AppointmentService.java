package com.MeetMate.appointment;

import com.MeetMate.appointment.sequence.SequenceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final SequenceService sequenceService;

  public Appointment getAppointment(long id) {
    return appointmentRepository.findAppointmentById(id)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
  }

  @Transactional
  public void createAppointment(long companyId, long clientId, Map<String, Object> appointmentData) throws IllegalAccessException {
    long appointmentId = sequenceService.getCurrentValue();

    Appointment appointment = new Appointment(appointmentId, companyId, clientId);
    Field[] fields = Appointment.class.getDeclaredFields();
    for(Field field : fields) {
      field.setAccessible(true);
      field.set(appointment, appointmentData.get(field.getName()));
    }
    
    appointmentRepository.save(appointment);
    sequenceService.incrementId();
  }
}

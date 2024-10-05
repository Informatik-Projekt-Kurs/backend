package com.MeetMate.appointment;

import com.MeetMate.appointment.sequence.AppointmentSequenceService;
import com.MeetMate.company.Company;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final AppointmentSequenceService appointmentSequenceService;
  private final MongoTemplate mongoTemplate;

  public Appointment getAppointment(long id) {
    return appointmentRepository.findAppointmentById(id)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
  }

  @Transactional
  public void createAppointment(long companyId, Map<String, Object> appointmentData) throws IllegalAccessException {
    long appointmentId = appointmentSequenceService.getCurrentValue();

    Appointment appointment = new Appointment(appointmentId, companyId);
    Field[] fields = Appointment.class.getDeclaredFields();
    for(Field field : fields) {
      field.setAccessible(true);
      field.set(appointment, appointmentData.get(field.getName()));
    }

    appointmentRepository.save(appointment);
    appointmentSequenceService.incrementId();
  }

  @Transactional
  public void editAppointment(long appointmentId, Map<String, Object> appointmentData) throws IllegalAccessException {
    Query query = new Query(Criteria.where("appointmentId").is(appointmentId));
    Update update = new Update();

    Field[] fields = Appointment.class.getDeclaredFields();
    for(Field field : fields) {
      field.setAccessible(true);
      update.set(field.getName(), appointmentData.get(field.getName()));
    }

    mongoTemplate.updateFirst(query, update, Company.class);
  }

  @Transactional
  public void deleteCompany(long id) {
    Appointment appointment = appointmentRepository.findAppointmentById(id)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

    appointmentRepository.delete(appointment);
  }
}

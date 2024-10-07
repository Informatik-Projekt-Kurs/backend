package com.MeetMate.appointment;

import com.MeetMate.appointment.sequence.AppointmentSequenceService;
import com.MeetMate.company.Company;
import com.MeetMate.company.CompanyRepository;
import com.MeetMate.enums.AppointmentStatus;
import com.MeetMate.enums.UserRole;
import com.MeetMate.security.JwtService;
import com.MeetMate.user.User;
import com.MeetMate.user.UserRepository;
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
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;

  public Appointment getAppointment(String token, long appointmentId) {
    if(!checkIfUserInAppointment(token, appointmentId))
      throw new IllegalArgumentException("User is not eligible to edit this appointment");
    return appointmentRepository.findAppointmentById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
  }

  @Transactional
  public void createAppointment(String from, String to, long companyId, long clientId, long assigneeId, String description, String location, AppointmentStatus status) throws IllegalAccessException {
    long appointmentId = appointmentSequenceService.getCurrentValue();

    Appointment appointment = new Appointment(appointmentId, companyId);
    if (from != null && !from.isEmpty()) appointment.setFrom(from);
    if (to != null && !to.isEmpty()) appointment.setTo(to);
    if (clientId != 0) appointment.setClientId(clientId);
    if (assigneeId != 0) appointment.setAssigneeId(assigneeId);
    if (description != null && !description.isEmpty()) appointment.setDescription(description);
    if (location != null && !location.isEmpty()) appointment.setLocation(location);
    if (status != null) appointment.setStatus(status);

    appointmentRepository.save(appointment);
    appointmentSequenceService.incrementId();
  }

  @Transactional
  public void editAppointment(String token, long appointmentId, String from, String to, long clientId, long assigneeId, String description, String location, AppointmentStatus status) throws IllegalAccessException {
    if(!checkIfUserInAppointment(token, appointmentId))
      throw new IllegalArgumentException("User is not eligible to edit this appointment");
    Query query = new Query(Criteria.where("appointmentId").is(appointmentId));
    Update update = new Update();

    if (from != null && !from.isEmpty()) update.set("from", from);
    if (to != null && !to.isEmpty()) update.set("to", to);
    if (clientId != 0) update.set("clientId", clientId);
    if (assigneeId != 0) update.set("assigneeId", assigneeId);
    if (description != null && !description.isEmpty()) update.set("description", description);
    if (location != null && !location.isEmpty()) update.set("location", location);
    if (status != null) update.set("status", status);


    mongoTemplate.updateFirst(query, update, Company.class);
  }

  @Transactional
  public void deleteAppointment(String token, long appointmentId) {
    if(!checkIfUserInAppointment(token, appointmentId))
      throw new IllegalArgumentException("User is not eligible to edit this appointment");
    Appointment appointment = appointmentRepository.findAppointmentById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

    appointmentRepository.delete(appointment);
  }

  private boolean checkIfUserInAppointment(String token, long appointmentId) throws IllegalArgumentException {
    String userEmail = jwtService.extractUserEmail(token);
    User user = userRepository.findUserByEmail(userEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));
    Appointment appointment = appointmentRepository.findAppointmentById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found!"));

    switch (user.getRole()) {
      case COMPANY_OWNER, COMPANY_MEMBER -> {
        if (appointment.getCompanyId() == user.getAssociatedCompany())
          return true;
      }
      case CLIENT -> {
        if (appointment.getClientId() == user.getId())
          return true;
      }
      default -> {
        throw new IllegalStateException("Role of user not found!");
      }
    }
    return false;
  }

}

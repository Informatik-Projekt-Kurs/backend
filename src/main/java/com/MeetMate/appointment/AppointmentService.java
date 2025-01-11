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

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final AppointmentSequenceService appointmentSequenceService;
  private final MongoTemplate mongoTemplate;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;

  public Appointment getAppointment(String token, long appointmentId) throws IllegalAccessException {
    User user = getUserFromToken(token);

    Appointment appointment = appointmentRepository.findAppointmentById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

    if (user.getId() != appointment.getClientId() && user.getAssociatedCompany() != appointment.getCompanyId())
      throw new IllegalAccessException("User is not eligible to view this appointment");

    return appointment;
  }

  @Transactional
  public void bookAppointment(String token, long appointmentId) throws IllegalAccessException {
    User user = getUserFromToken(token);

    Appointment appointment = appointmentRepository.findAppointmentById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

    if (user.getRole() != UserRole.CLIENT)
      throw new IllegalAccessException("Only clients can book appointments");

    Query query = new Query(Criteria.where("id").is(appointmentId));
    Update update = new Update();

    update.set("clientId", user.getId());
    update.set("status", AppointmentStatus.BOOKED);

    mongoTemplate.updateFirst(query, update, Appointment.class);
  }

  @Transactional
  public void createAppointment(String token, Instant from, Instant to, long clientId, String description, String location) {
    Company company = getCompanyFromToken(token);

    long appointmentId = appointmentSequenceService.getCurrentValue();

    Appointment appointment = new Appointment(appointmentId, company.getId());
    if (from != null) appointment.setFrom(from);
    if (to != null) appointment.setTo(to);
    if (clientId != 0) appointment.setClientId(clientId);
    if (description != null && !description.isEmpty()) appointment.setDescription(description);
    if (location != null && !location.isEmpty()) appointment.setLocation(location);
    appointment.setStatus(AppointmentStatus.PENDING);

    appointmentRepository.save(appointment);
    appointmentSequenceService.incrementId();
  }

  @Transactional
  public void editAppointment(String token, long appointmentId, Instant from, Instant to, long clientId, String description, String location, AppointmentStatus status) {
    Company company = getCompanyFromToken(token);

    if (appointmentNotOfCompany(company, appointmentId))
      throw new IllegalArgumentException("User is not eligible to edit this appointment");

    Query query = new Query(Criteria.where("id").is(appointmentId));
    Update update = new Update();

    if (from != null) update.set("from", from);
    if (to != null) update.set("to", to);
    if (clientId != 0) update.set("clientId", clientId);
    if (description != null && !description.isEmpty()) update.set("description", description);
    if (location != null && !location.isEmpty()) update.set("location", location);
    if (status != null) update.set("status", status);

    mongoTemplate.updateFirst(query, update, Appointment.class);
  }

  @Transactional
  public void deleteAppointment(String token, long appointmentId) {
    Company company = getCompanyFromToken(token);
    if (appointmentNotOfCompany(company, appointmentId))
      throw new IllegalArgumentException("User is not eligible to edit this appointment");
    Appointment appointment = appointmentRepository.findAppointmentById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

    appointmentRepository.delete(appointment);
  }

  private boolean appointmentNotOfCompany(Company company, long appointmentId) throws IllegalArgumentException {
    Appointment appointment = appointmentRepository.findAppointmentById(appointmentId)
        .orElseThrow(() -> new EntityNotFoundException("Appointment not found!"));

    return appointment.getCompanyId() != company.getId();
  }

  private User getUserFromToken(String token) {
    String email = jwtService.extractUserEmail(token);
    return userRepository.findUserByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

  private Company getCompanyFromToken(String token) {
    String userEmail = jwtService.extractUserEmail(token);
    User user = userRepository.findUserByEmail(userEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));
    if (user.getAssociatedCompany() == -1L)
      throw new EntityNotFoundException("User is not associated with a company");
    return companyRepository.findCompanyById(user.getAssociatedCompany())
        .orElseThrow(() -> new EntityNotFoundException("Company not found!"));
  }

}

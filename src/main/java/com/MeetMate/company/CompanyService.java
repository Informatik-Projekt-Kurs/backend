package com.MeetMate.company;

import com.MeetMate.appointment.Appointment;
import com.MeetMate.appointment.AppointmentRepository;
import com.MeetMate.company.sequence.CompanySequenceService;
import com.MeetMate.enums.AppointmentStatus;
import com.MeetMate.enums.BusinessType;
import com.MeetMate.enums.UserRole;
import com.MeetMate.response.GetResponse;
import com.MeetMate.security.JwtService;
import com.MeetMate.user.User;
import com.MeetMate.user.UserController;
import com.MeetMate.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.MongoTransactionException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

  private final UserController userController;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final MongoTemplate mongoTemplate;
  private final CompanySequenceService companySequenceService;
  private final JwtService jwtService;
  private final AppointmentRepository appointmentRepository;

  public Company getCompany(long id) throws IllegalArgumentException {
    return companyRepository.findCompanyById(id)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
  }

  public ArrayList<Appointment> getAvailableAppointments(long id, Instant date) {
    ArrayList<Appointment> appointments = appointmentRepository.findAppointmentsByCompanyId(id);
    ArrayList<Appointment> availableAppointments = new ArrayList<>();

    Instant appointmentTime;
    boolean isSameDay;
    // Get start of current day
    LocalDate today = LocalDate.now(ZoneId.systemDefault());
    Instant startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant();

    for (Appointment appointment : appointments) {
      appointmentTime = appointment.getFrom();
      isSameDay = date == null ||
          LocalDate.ofInstant(date, ZoneId.systemDefault())
              .equals(LocalDate.ofInstant(appointmentTime, ZoneId.systemDefault()));

      if (appointment.getStatus() == AppointmentStatus.PENDING
          && appointmentTime.isAfter(startOfToday)  // Compare with start of day instead
          && isSameDay)
        availableAppointments.add(appointment);
    }
    return availableAppointments;
  }

  public ArrayList<Appointment> getAllAppointments(long id) {
    return appointmentRepository.findAppointmentsByCompanyId(id);
  }

  public ArrayList<GetResponse> getClients(String token) throws IllegalAccessException {
    String email = jwtService.extractUserEmail(token);

    User companyMember = userRepository.findUserByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"));

    Company company = companyRepository.findCompanyById(companyMember.getAssociatedCompany())
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    if (isNotCompanyOwner(email)
        && isNotCompanyMember(company, userRepository.findUserByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"))
        .getId()))
      throw new IllegalAccessException("Not a company member");

    ArrayList<Appointment> appointments = appointmentRepository.findAppointmentsByCompanyId(company.getId());
    ArrayList<User> clients = new ArrayList<>();

    User client;
    for (Appointment appointment : appointments) {
      client = userRepository.findUserById(appointment.getClientId()).orElse(null);
      if (clients.contains(client)) continue;
      clients.add(client);
    }

    if (clients.isEmpty())
      return new ArrayList<GetResponse>();

    ArrayList<GetResponse> response = new ArrayList<>();

    for (User user : clients) {
      response.add(GetResponse.builder()
          .id(user.getId())
          .name(user.getName())
          .email(user.getEmail())
          .build());
    }

    return response;
  }

  public List<Company> getCompanies() {
    return companyRepository.findAll();
  }

  @Transactional
  public void createCompany(String companyName, String ownerEmail, String ownerName, String ownerPassword) {
    if (userRepository.findUserByEmail(ownerEmail).isPresent())
      throw new IllegalArgumentException("Email already taken");

    long companyId = companySequenceService.getCurrentValue();
    //Create the company owner
    MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
    ownerData.add("email", ownerEmail);
    ownerData.add("name", ownerName);
    ownerData.add("password", ownerPassword);
    ownerData.add("role", UserRole.COMPANY_OWNER.toString());
    ownerData.add("associatedCompany", String.valueOf(companyId));

    userController.registerNewUser(ownerData);
    long ownerId = userRepository.findUserByEmail(ownerEmail)
        .orElseThrow(() -> new IllegalStateException("Owner could not be created correctly!"))
        .getId();

    companyRepository.save(new Company(companyId, companyName, ownerEmail, ownerId));
    companySequenceService.incrementId();
  }

  @Transactional
  public void editCompany(String token, String companyName, String description, String businessType) throws IllegalAccessException {
    String ownerEmail = jwtService.extractUserEmail(token);

    if (isNotCompanyOwner(ownerEmail))
      throw new IllegalAccessException("Not a company owner");

    Company company = companyRepository.findCompanyByOwnerEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    Query query = new Query(Criteria.where("ownerEmail").is(ownerEmail));
    Update update = new Update();
    if (companyName != null && !companyName.isEmpty()) update.set("name", companyName);
    if (description != null && !description.isEmpty()) update.set("description", description);
    if (businessType != null && !businessType.isEmpty()) update.set("businessType", BusinessType.valueOf(businessType));
    mongoTemplate.updateFirst(query, update, Company.class);
  }

  @Transactional
  public void deleteCompany(String token) throws IllegalAccessException {
    String ownerEmail = jwtService.extractUserEmail(token);

    if (isNotCompanyOwner(ownerEmail))
      throw new IllegalAccessException("Not a company owner");

    Company company = companyRepository.findCompanyByOwnerEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    try {
      userController.deleteUser("Bearer:" + token);
    } catch (Throwable t) {
      throw new MongoTransactionException("Could not delete company owner");
    }

    try {
      for (Long memberId : company.getMemberIds())
        userRepository.deleteById(memberId);
    } catch (Throwable t) {
      throw new MongoTransactionException("Could not delete company members");
    }
    companyRepository.delete(company);
  }

  ////////////////MEMBER MANAGEMENT////////////////

  public GetResponse getMember(String token, long memberId) throws IllegalAccessException {
    String email = jwtService.extractUserEmail(token);

    Company company = companyRepository.findCompanyByOwnerEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    if (isNotCompanyOwner(email)
        && isNotCompanyMember(company, userRepository.findUserByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found"))
        .getId()))
      throw new IllegalAccessException("Not a company member");

    if (isNotCompanyMember(company, memberId))
      throw new EntityNotFoundException("Not a company member");

    return getMemberById(memberId);
  }

  public ArrayList<GetResponse> getAllMembers(String token) throws IllegalAccessException {
    String email = jwtService.extractUserEmail(token);

    Company company = companyRepository.findCompanyByOwnerEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    if (isNotCompanyOwner(email)
        && isNotCompanyMember(company, userRepository.findUserByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"))
        .getId()))
      throw new IllegalAccessException("Not a company member");

    ArrayList<GetResponse> members = new ArrayList<>();
    for (Long memberId : company.getMemberIds()) {
      GetResponse member = getMemberById(memberId);
      members.add(member);
    }
    return members;
  }

  @Transactional
  public void addMember(String token, String memberEmail, String memberName, String memberPassword) throws IllegalAccessException {
    String ownerEmail = jwtService.extractUserEmail(token);

    if (isNotCompanyOwner(ownerEmail))
      throw new IllegalAccessException("Not a company owner");

    Company company = companyRepository.findCompanyByOwnerEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    MultiValueMap<String, String> memberData = new LinkedMultiValueMap<>();
    memberData.add("email", memberEmail);
    memberData.add("name", memberName);
    memberData.add("password", memberPassword);
    memberData.add("role", UserRole.COMPANY_MEMBER.toString());
    memberData.add("associatedCompany", String.valueOf(company.getId()));

    userController.registerNewUser(memberData);

    Query query = new Query(Criteria.where("ownerEmail").is(company.getOwnerEmail()));
    Update update = new Update();

    Long memberId = userRepository.findUserByEmail(memberEmail)
        .orElseThrow(() -> new IllegalStateException("Member could not be created correctly!"))
        .getId();

    ArrayList<Long> a = company.getMemberIds();
    if (a.contains(memberId))
      throw new IllegalStateException("Member already exists");

    a.add(memberId);
    update.set("memberIds", a);

    mongoTemplate.updateFirst(query, update, Company.class);
  }

  @Transactional
  public void deleteMember(String token, long memberId) throws IllegalAccessException {
    String ownerEmail = jwtService.extractUserEmail(token);

    if (isNotCompanyOwner(ownerEmail))
      throw new IllegalAccessException("Not a company owner");

    Company company = companyRepository.findCompanyByOwnerEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));

    if (isNotCompanyMember(company, memberId))
      throw new IllegalAccessException("Not a member of the company");

    User member = userRepository.findUserById(memberId)
        .orElseThrow(() -> new EntityNotFoundException("Member not found"));

    userRepository.delete(member);

    Query query = new Query(Criteria.where("ownerEmail").is(company.getOwnerEmail()));
    Update update = new Update();
    ArrayList<Long> a = company.getMemberIds();
    a.remove(memberId);
    update.set("memberIds", a);

    mongoTemplate.updateFirst(query, update, Company.class);
  }

  private boolean isNotCompanyOwner(String email) {
    return userRepository.findUserByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"))
        .getRole() != UserRole.COMPANY_OWNER;
  }

  private boolean isNotCompanyMember(Company company, long memberId) {
    return !company.getMemberIds().contains(memberId);
  }

  private GetResponse getMemberById(long id) {
    User user = userRepository.findUserById(id)
        .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

    return GetResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .created_at(user.getCreatedAt())
        .email(user.getEmail())
        .role(user.getRole())
        .associatedCompany(user.getAssociatedCompany())
        .build();
  }
}

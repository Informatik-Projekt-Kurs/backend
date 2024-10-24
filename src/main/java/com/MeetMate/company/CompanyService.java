package com.MeetMate.company;

import com.MeetMate.company.sequence.CompanySequenceService;
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

  public Company getCompany(long id) throws IllegalArgumentException {
    return companyRepository.findCompanyById(id)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
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
  public void editCompany(String token, String companyName, String description, String businessType) {
    String ownerEmail = getCompanyWithToken(token).getOwnerEmail();

    Query query = new Query(Criteria.where("ownerEmail").is(ownerEmail));
    Update update = new Update();
    if (companyName != null && !companyName.isEmpty()) update.set("name", companyName);
    if (description != null && !description.isEmpty()) update.set("description", description);
    if (businessType != null && !businessType.isEmpty()) update.set("businessType", BusinessType.valueOf(businessType));
    mongoTemplate.updateFirst(query, update, Company.class);
  }

  @Transactional
  public void deleteCompany(String token) {
    Company company = getCompanyWithToken(token);
    try {
      userController.deleteUser("Bearer:" + token);
    } catch (Throwable t) {
      throw new MongoTransactionException("Could not delete company owner");
    }
    companyRepository.delete(company);
  }

  public GetResponse getMember(String token, long memberId) {
    Company company = getCompanyWithToken(token);

    if (isNotCompanyMember(company, memberId))
      throw new EntityNotFoundException("Not a company member!");

    return getMemberById(memberId);
  }

  public ArrayList<GetResponse> getAllMembers(String token) {
    Company company = getCompanyWithToken(token);

    ArrayList<GetResponse> members = new ArrayList<>();
    for (Long memberId : company.getMemberIds()) {
      GetResponse member = getMemberById(memberId);
      members.add(member);
    }
    return members;
  }

  @Transactional
  public void addMember(String token, String memberEmail, String memberName, String memberPassword) {
    Company company = getCompanyWithToken(token);

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
    Company company = getCompanyWithToken(token);

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

  private Company getCompanyWithToken(String token) throws IllegalArgumentException {
    String ownerEmail = jwtService.extractUserEmail(token);
    if (userRepository.findUserByEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"))
        .getRole() != UserRole.COMPANY_OWNER)
      throw new IllegalArgumentException("User is not a company owner");

    return companyRepository.findCompanyByOwnerEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
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

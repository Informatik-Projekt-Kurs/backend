package com.MeetMate.company;

import com.MeetMate.company.sequence.SequenceService;
import com.MeetMate.enums.BusinessType;
import com.MeetMate.enums.UserRole;
import com.MeetMate.user.UserController;
import com.MeetMate.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class CompanyService {

  private final UserController userController;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final MongoTemplate mongoTemplate;
  private final SequenceService sequenceService;

  public Company getCompany(long id) throws IllegalArgumentException {

    //Test if user is a company owner
    if (userRepository.findUserById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"))
        .getRole() != UserRole.COMPANY_OWNER)
      throw new IllegalArgumentException("User is not a company owner");

    return companyRepository.findCompanyById(id)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
  }

  @Transactional
  public void createCompany(String companyName, String ownerEmail, String ownerName, String ownerPassword) {
    long companyId = sequenceService.getCurrentValue();
    //Create the company owner
    MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
    ownerData.add("email", ownerEmail);
    ownerData.add("name", ownerName);
    ownerData.add("password", ownerPassword);
    ownerData.add("role", UserRole.COMPANY_OWNER.toString());
    ownerData.add("associatedCompany", String.valueOf(companyId));

    userController.registerNewUser(ownerData);

    companyRepository.save(new Company(companyId, companyName, ownerEmail));
    sequenceService.incrementId();
  }

  @Transactional
  public void editCompany(String token, String companyName, String description, String businessType) {
    String ownerEmail = getCompanyWithToken(token).getOwnerEmail();

    Query query = new Query(Criteria.where("ownerEmail").is(ownerEmail));
    Update update = new Update();
    if (companyName != null) update.set("name", companyName);
    if (description != null) update.set("description", description);
    if (businessType != null) update.set("businessType", BusinessType.valueOf(businessType));
    mongoTemplate.updateFirst(query, update, Company.class);
  }

  @Transactional
  public void deleteCompany(String token) {
    Company company = getCompanyWithToken(token);
    try {
      userController.deleteUser(token);
    } catch (Throwable t) {
      return;
    }
    companyRepository.delete(company);
  }

  private Company getCompanyWithToken(String ownerEmail) throws IllegalArgumentException {

    //Test if user is a company owner
    if (userRepository.findUserByEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"))
        .getRole() != UserRole.COMPANY_OWNER)
      throw new IllegalArgumentException("User is not a company owner");

    return companyRepository.findCompanyByOwnerEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
  }
}

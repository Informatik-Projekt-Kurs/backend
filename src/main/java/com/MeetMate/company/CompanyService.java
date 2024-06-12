package com.MeetMate.company;

import com.MeetMate.enums.BusinessType;
import com.MeetMate.enums.UserRole;
import com.MeetMate.security.JwtService;
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
  private final JwtService jwtService;
  private final MongoTemplate mongoTemplate;

  public Company getCompany(String token) throws IllegalArgumentException {
    String ownerEmail = jwtService.extractUserEmail(token);

    //Test if user is a company owner
    if (userRepository.findUserByEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("User not found!"))
        .getRole() != UserRole.COMPANY_OWNER)
      throw new IllegalArgumentException("User is not a company owner");

    return companyRepository.findCompanyByOwnerEmail(ownerEmail)
        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
  }

  @Transactional
  public void createCompany(String companyName, String ownerEmail, String ownerName, String ownerPassword) {
    //Create the company owner
    MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
    ownerData.add("email", ownerEmail);
    ownerData.add("name", ownerName);
    ownerData.add("password", ownerPassword);
    ownerData.add("role", UserRole.COMPANY_OWNER.toString());

    userController.registerNewUser(ownerData);

    companyRepository.save(new Company(companyName, ownerEmail));
  }

  @Transactional
  public void editCompany(String token, String companyName, String description, String businessType) {
    Company company = getCompany(token);
    String ownerEmail = jwtService.extractUserEmail(token);
//        if(companyName != null) company.setName(companyName);
//        if(description != null) company.setDescription(description);
//        if(businessType != null) company.setBusinessType(BusinessType.valueOf(businessType));
//        companyRepository.save(company);

    Query query = new Query(Criteria.where("ownerEmail").is(ownerEmail));
    Update update = new Update();
    if (companyName != null) update.set("name", companyName);
    if (description != null) update.set("description", description);
    if (businessType != null) update.set("businessType", BusinessType.valueOf(businessType));
    mongoTemplate.updateFirst(query, update, Company.class);
  }

  @Transactional
  public void deleteCompany(String token) {
    Company company = getCompany(token);
    try {
      userController.deleteUser(token);
    } catch (Throwable t) {
      return;
    }
    companyRepository.delete(company);
  }
}

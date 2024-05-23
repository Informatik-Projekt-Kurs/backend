package com.MeetMate.company;

import com.MeetMate.enums.UserRole;
import com.MeetMate.security.JwtService;
import com.MeetMate.user.UserController;
import com.MeetMate.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public void createCompany(String companyName, String ownerEmail, String ownerName, String ownerPassword) {
        //Create the company owner
        MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
        ownerData.add("email", ownerEmail);
        ownerData.add("name", ownerName);
        ownerData.add("password", ownerPassword);
        ownerData.add("role", UserRole.COMPANY_OWNER.toString());

        //Get id of company Owner
        userController.registerNewUser(ownerData);

        companyRepository.save(new Company(companyName, ownerEmail));
    }

}

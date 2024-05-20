package com.MeetMate.company;

import com.MeetMate.security.JwtService;
import com.MeetMate.user.UserController;
import com.MeetMate.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InaccessibleObjectException;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserController userController;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JwtService jwtService;

    public Company getCompany(String token) throws IllegalArgumentException {
        //Throws IllegalArgumentException if user is not a company owner
        long id = jwtService.extractCompanyId(token);

        return companyRepository.findCompanyById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
    }

    public void createCompany(String companyName, String ownerEmail, String ownerName, String ownerPassword) {
        //Create the company owner
        MultiValueMap<String, String> ownerData = new LinkedMultiValueMap<>();
        ownerData.add("email", ownerEmail);
        ownerData.add("name", ownerName);
        ownerData.add("password", ownerPassword);

        //Get id of company Owner
        userController.registerNewUser(ownerData);
        long ownerId = userRepository.findUserByEmail(ownerEmail)
                .orElseThrow(() -> new InaccessibleObjectException("Company owner Creation failed"))
                .getId();

        companyRepository.save(new Company(companyName, ownerId));
    }


}

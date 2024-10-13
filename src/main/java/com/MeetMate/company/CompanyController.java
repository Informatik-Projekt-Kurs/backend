package com.MeetMate.company;

import com.MeetMate.enums.UserRole;
import com.MeetMate.response.GetResponse;
import com.MeetMate.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.InaccessibleObjectException;

@Controller
@RequestMapping(path = "api/company")
@RequiredArgsConstructor
public class CompanyController {

  private final CompanyService companyService;

  @QueryMapping
  public Company getCompany(@Argument long id) {
    try {
      return companyService.getCompany(id);

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      return new Company(-1, "error", "error");

//            if (tc == EntityNotFoundException.class)
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

//            if (tc == IllegalArgumentException.class)
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> createCompany(
      @Argument String companyName,
      @Argument String ownerEmail,
      @Argument String ownerName,
      @Argument String ownerPassword) {
    try {
      companyService.createCompany(companyName, ownerEmail, ownerName, ownerPassword);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      if (tc == InaccessibleObjectException.class)
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> editCompany(
      @ContextValue String token,
      @Argument String companyName,
      @Argument String description,
      @Argument String businessType) {
    token = token.substring(7);
    try {
      companyService.editCompany(token, companyName, description, businessType);
      return ResponseEntity.ok().build();
    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();

      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      if (tc == IllegalArgumentException.class)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  @MutationMapping
  public ResponseEntity<?> deleteCompany(@ContextValue String token) {
    token = token.substring(7);
    try {
      companyService.deleteCompany(token);
      return ResponseEntity.ok().build();

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      if (tc == EntityNotFoundException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      if (tc == IllegalArgumentException.class)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
    }
  }

  ////////////////MEMBER MANAGEMENT////////////////

  @QueryMapping
  public GetResponse getMember(
      @ContextValue String token,
      @Argument long memberId
  ) {
    token = token.substring(7);
    try {
      return companyService.getMember(token, memberId);

    } catch (Throwable t) {
      Class<? extends Throwable> tc = t.getClass();
      return null;
    }
  }

    @MutationMapping
    public ResponseEntity<?> addMember (
        @ContextValue String token,
        @Argument String memberEmail,
        @Argument String memberName,
        @Argument String memberPassword){
      token = token.substring(7);
      try {
        companyService.addMember(token, memberEmail, memberName, memberPassword);
        return ResponseEntity.ok().build();

      } catch (Throwable t) {
        Class<? extends Throwable> tc = t.getClass();

        if (tc == EntityNotFoundException.class)
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());

        if (tc == IllegalArgumentException.class)
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
      }
    }

  }

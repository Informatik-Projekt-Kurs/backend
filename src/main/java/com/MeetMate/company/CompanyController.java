package com.MeetMate.company;

import com.MeetMate._experiments.Experimentational;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.ConditionalOnGraphQlSchema;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InaccessibleObjectException;

@RestController
@RequestMapping(path = "api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Experimentational
    @GetMapping(path = "test")
    public Company test(@RequestHeader(name = "Authorization") String token) {
        token = token.substring(7);
        return companyService.getCompany(token);
    }

    @QueryMapping
    public Company getCompany(@ContextValue(name = "token") String token) {
        token = token.substring(7);
        try {
            var c = companyService.getCompany(token);
            System.out.println(c);
            return c;

        } catch (Throwable t) {
            Class<? extends Throwable> tc = t.getClass();
            return null;
//            if (tc == EntityNotFoundException.class)
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("message: " + t.getMessage());
//
//            if (tc == IllegalArgumentException.class)
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message: " + t.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("message: " + t.getMessage());
        }
    }

    @MutationMapping
    public ResponseEntity<?> createCompany(@Argument String companyName, @Argument String ownerEmail, @Argument String ownerName, @Argument String ownerPassword) {
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


}

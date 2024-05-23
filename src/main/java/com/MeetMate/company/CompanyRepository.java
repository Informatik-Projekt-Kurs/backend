package com.MeetMate.company;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, Long> {

    Optional<Company> findCompanyByOwnerEmail(String ownerEmail);
}

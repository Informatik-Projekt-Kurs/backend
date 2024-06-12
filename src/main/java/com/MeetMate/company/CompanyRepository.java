package com.MeetMate.company;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, Long> {

    Optional<Company> findCompanyByOwnerEmail(String ownerEmail);

//    @Modifying
//    @Query("{ 'ownerEmail': :#{#ownerEmail} }")
//    Company updateByOwnerEmail(String ownerEmail);
}

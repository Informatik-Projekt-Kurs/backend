package com.MeetMate.company;

import com.MeetMate.enums.BusinessType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "companies")
@Data
public class Company {
    @Id
    @Indexed(direction = IndexDirection.ASCENDING)
    private long id;
    private String name;
    private String description;
    private BusinessType businessType;
    private String[] memberEmails;
    @Indexed(unique = true)
    private String ownerEmail;

    public Company(String name, String ownerEmail) {
        this.name = name;
        this.ownerEmail = ownerEmail;
    }
}

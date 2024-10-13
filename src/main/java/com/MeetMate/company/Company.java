package com.MeetMate.company;

import com.MeetMate.enums.BusinessType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "companies")
@Data
@NoArgsConstructor
public class Company {
    private long id;
    private String name;
    private String description;
    private BusinessType businessType;
    private ArrayList<String> memberEmails;
    @Indexed(unique = true)
    private String ownerEmail;

    public Company(long id,String name, String ownerEmail) {
        this.id = id;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.description = "";
        this.businessType = null;
    }
}

package com.MeetMate.company;

import com.MeetMate.enums.BusinessType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "companies")
@Data
public class Company {
    @Id
    private long id;
    private String name;
    private String description;
    private BusinessType businessType;
    private long[] members;
    private long ownerId;

    public Company(String name, long ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }
}

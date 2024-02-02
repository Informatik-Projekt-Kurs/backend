package com.MeetMate.response;

import com.MeetMate.roles.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class GetResponse {

    long id;
    String name;
    LocalDate created_at;
    String email;
    Role role;
}

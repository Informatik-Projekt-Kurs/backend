package com.MeetMate.response;

import com.MeetMate.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@Builder
public class GetResponse {

  long id;
  String name;
  LocalDate created_at;
  String email;
  UserRole role;
  long associatedCompany;
  ArrayList<Long> subscribedCompanies;
}
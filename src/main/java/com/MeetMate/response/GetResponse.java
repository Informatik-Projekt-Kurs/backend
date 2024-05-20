package com.MeetMate.response;

import com.MeetMate.enums.UserRole;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GetResponse {

  long id;
  String name;
  LocalDate created_at;
  String email;
  UserRole role;
}

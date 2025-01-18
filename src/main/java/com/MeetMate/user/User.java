package com.MeetMate.user;

import com.MeetMate.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {
  @Id
  @SequenceGenerator(name = "user_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
  private long id;
  private String name;
  private String email;
  private String password;
  private LocalDate createdAt;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  private String refreshToken;
  private long associatedCompany;
  private ArrayList<Long> subscribedCompanies;

  public User(String name, String email, String password, UserRole role) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.createdAt = LocalDate.now();
    this.role = role;
    subscribedCompanies = new ArrayList<>();
  }

  public Map<String, Object> generateMap() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("name", this.getName());
    map.put("password", this.getPassword());
    map.put("role", this.getRole());
    return map;
  }

  // List of Roles
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}

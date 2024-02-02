package com.MeetMate.user;

import com.MeetMate.experiments.Experimentational;
import com.MeetMate.roles.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
  @Id
  @SequenceGenerator(name = "user_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
  private Long id;

  private String name;

  private LocalDate createdAt;

  private String email;
  private String password;
  private String refreshToken;

  @Enumerated(EnumType.STRING)
  private Role role;

  // Last login
  // bool verified

  public User() {}

  @Experimentational
  public User(Long id, String name, String email, String password) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.createdAt = LocalDate.now();
  }

  public User(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.createdAt = LocalDate.now();
    role = Role.CLIENT;
  }

  @Experimentational
  public User(String email, String password) {
    this.email = email;
    this.password = password;
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

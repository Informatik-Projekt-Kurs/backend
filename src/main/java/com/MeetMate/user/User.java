package com.MeetMate.user;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;

@Entity
@Table(name = "users")
public class User {
  @Id
  @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
  private Long id;

  private String name;
  private LocalDate birthday;
  private LocalDate createdAt;
  private String email;
  private String password;
  // enum Rolle
  // Last login
  // (refresh token)
  // bool verified

  // No need for column in database
  @Transient private int age;

  public User() {}

  public User(Long id, String name, LocalDate birthday, String email, String password) {
    this.id = id;
    this.name = name;
    this.birthday = birthday;
    this.email = email;
    this.password = password;
    this.createdAt = LocalDate.now();
    if (birthday == null) {
      birthday = LocalDate.of(1970, Month.JANUARY, 1);
    }
  }

  public User(String name, LocalDate birthday, String email, String password) {
    this.name = name;
    this.birthday = birthday;
    this.email = email;
    this.password = password;
    this.createdAt = LocalDate.now();
  }

  public User(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getBirthday() {
    if (this.birthday == null) birthday = LocalDate.EPOCH;
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getAge() {
    return Period.between(getBirthday(), LocalDate.now()).getYears();
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", birthday="
        + birthday
        + ", createdAt="
        + createdAt
        + ", email='"
        + email
        + '\''
        + ", password='"
        + password
        + '\''
        + ", age="
        + age
        + '}';
  }
}

package com.MeetMate.user;

import com.MeetMate.roles.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String name;
    private LocalDate birthday;
    @Setter(AccessLevel.NONE)
    private LocalDate createdAt;
    private String email;
    private String password;
    private Role role;
    //Last login
    //(refresh token)
    //bool verified
    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int age;

    public User() {
    }

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

    public int getAge() {
        return Period.between(getBirthday(), LocalDate.now()).getYears();
    }

}

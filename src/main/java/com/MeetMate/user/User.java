package com.MeetMate.user;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class User {
    private Long id;
    private List<String> name;
    private LocalDate birthday;
    private String email;
    private int age;

    public User() {
    }

    public User(Long id,
                List<String> name,
                LocalDate birthday,
                String email) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        age = (int)Math.floor(Period.between(birthday, LocalDate.now()).toTotalMonths()/12);
    }

    public User(List<String> name,
                LocalDate birthday,
                String email) {
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        age = (int)Math.floor(Period.between(birthday, LocalDate.now()).toTotalMonths()/12);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
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

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}

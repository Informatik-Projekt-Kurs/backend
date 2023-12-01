package com.MeetMate.user;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
public class UserService {

    public List<User> getUsers(){
        return List.of(
                new User(
                        1L,
                        List.of("Carl", "B"),
                        LocalDate.of(2006, Month.OCTOBER, 14),
                        "carl.b@gmail.com"
                )
        );
    }
}

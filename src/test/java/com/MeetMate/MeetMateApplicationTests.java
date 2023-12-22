package com.MeetMate;

import com.MeetMate.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;

@SpringBootTest
class MeetMateApplicationTests {

    @Test
    void contextLoads() {
        User test = new User(
                "Carl A",
                LocalDate.of(2000, Month.JANUARY, 21),
                "carl.A@gmail.com",
                "carl123");
                System.out.println(test.getAge());
        System.out.println(test.getName());
    }

}

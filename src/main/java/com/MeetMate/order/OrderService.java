package com.MeetMate.order;

import com.MeetMate.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
public class OrderService {

    public List<Order> getOrders() {
        return List.of(
                new Order(
                        1234567890L,
                        new User(1L, "Carl B", LocalDate.of(2006, Month.OCTOBER, 14), "hello@gmail.com", "carl123")
                )
        );
    }

}

package com.MeetMate._experiments;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "test")
public class Test {
  //Link in SecurityConfig.java
  @PostMapping(path = "test")
  public String getUser(){
      return "asdasdasd";
  }

}

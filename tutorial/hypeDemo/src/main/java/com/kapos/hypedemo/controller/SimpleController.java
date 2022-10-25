package com.kapos.hypedemo.controller;

import com.kapos.hypedemo.model.User;
import com.kapos.hypedemo.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    private final UserRepository userRepository;

    @Autowired
    public SimpleController(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @PostMapping("/hello")
    public String helloWorld() {
        return "Hello World";
    }

    @PostMapping("/user/example")
    public User insertExampleUser(){
        return userRepository.save(new User("szilardKorom","korom.szilard@gmail.com","gzv970"));
    }

    @GetMapping("/user/{id}")
    public User findById(@PathVariable Integer id){
        return userRepository.findById(id).orElse(null);
    }
}

package com.kapos.hypedemo.controller;

import com.kapos.hypedemo.model.User;
import com.kapos.hypedemo.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;

@RestController
public class SimpleController {

    private final UserRepository userRepository;

    @Autowired
    public SimpleController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/hello")
    public String helloWorld() {
        return "Hello World";
    }

    @PostMapping("/user/example")
    public User insertExampleUser() {
        return userRepository.save(new User("hype", "hype", "hype", "hype@gmail.com", "hypejelszo", false, null));
    }

    // Felhasználó létrehozása GET method-dal
    @GetMapping("/createuser/{username}/{password}")
    public User insertExampleUser(@PathVariable String username, @PathVariable String password) {
        return userRepository.save(new User("hype", "hype", "hype", "hype@gmail.com", "hypejelszo", false, null));

    }

    @GetMapping("/register")
    public ModelAndView register(@ModelAttribute User user, Model model) {

        model.addAttribute("user", user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register.html");

        return modelAndView;
    }

    @PostMapping("/register")
    public User insertuser(User user) {
        return userRepository.save(new User(user.getUserName(), user.getFirstName(), user.getSecondName(), user.getEmail(), user.getPassword(), user.isGender(), user.getBorn()));
    }

    @GetMapping("/user/{id}")
    public User findById(@PathVariable Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}

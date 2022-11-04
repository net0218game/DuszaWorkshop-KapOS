package com.kapos.hypedemo.controller;

import com.kapos.hypedemo.model.Chat;
import com.kapos.hypedemo.model.User;
import com.kapos.hypedemo.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @GetMapping("/")
    public ModelAndView index() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView register(@ModelAttribute User user, Model model) {

        model.addAttribute("user", user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register.html");
        return modelAndView;
    }

    @GetMapping("/chat")
    public ModelAndView chat() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("chat.html");
        return modelAndView;
    }

    @PostMapping("/register")
    public User insertUser(User user) {
        return userRepository.save(new User(user.getUserName(), user.getFirstName(), user.getSecondName(), user.getEmail(), user.getPassword(), user.isGender(), null));
    }


    @GetMapping("/user/{id}")
    public User findById(@PathVariable Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Chat sendMessage(@Payload Chat chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Chat addUser(@Payload Chat chatMessage, SimpMessageHeaderAccessor headerAccessor, Chat chat) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chat.getSender());
        return chatMessage;
    }
}

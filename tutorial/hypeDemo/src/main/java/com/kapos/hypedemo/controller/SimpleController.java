package com.kapos.hypedemo.controller;

import com.kapos.hypedemo.model.Chat;
import com.kapos.hypedemo.model.User;
import com.kapos.hypedemo.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class SimpleController {

    private final UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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
        return userRepository.save(new User("hype", "hype", "hype", "hype@gmail.com", "hypejelszo", "Male", null));
    }

    // Felhasználó létrehozása GET method-dal
    @GetMapping("/createuser/{username}/{password}")
    public User insertExampleUser(@PathVariable String username, @PathVariable String password) {
        return userRepository.save(new User("hype", "hype", "hype", "hype@gmail.com", "hypejelszo", "Male", null));
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

    @GetMapping("/hype")
    public ModelAndView hype() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("hype.html");
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


    // A Public Chat része
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Chat sendMessage(@Payload Chat chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/application.addUser")
    @SendTo("/chat/public")
    public Chat addUser(@Payload Chat chatMessage, SimpMessageHeaderAccessor headerAccessor, Chat chat) {
        // felhasznalo hozzaadasa session-höz (?)
        headerAccessor.getSessionAttributes().put("username", chat.getSender());
        return chatMessage;
    }

    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Chat chat) {
        simpMessagingTemplate.convertAndSendToUser(chat.getReceiver(), "/specific", chat);
    }

    @ModelAttribute("contacts")
    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
package com.kapos.hypedemo.controller;

import com.kapos.hypedemo.model.Chat;
import com.kapos.hypedemo.model.User;
import com.kapos.hypedemo.model.repo.MessagesRepository;
import com.kapos.hypedemo.model.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class SimpleController {
    private final UserRepository userRepository;
    private final MessagesRepository messagesRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public SimpleController(UserRepository userRepository, MessagesRepository messagesRepository) {
        this.userRepository = userRepository;
        this.messagesRepository = messagesRepository;
    }

    // ========== Eleresi utak ==========
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

    // ========== Eleresi utak vege ==========

    // Felhasznalok Kilistazasa
    @GetMapping("/contacts")
    public ModelAndView contact() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contacts.html");
        return modelAndView;
    }

    // Regisztralas
    @PostMapping("/register")
    public User insertUser(User user) {
        return userRepository.save(new User(user.getUserName(), user.getFirstName(), user.getSecondName(), user.getEmail(), user.getPassword(), user.getGender(), user.getBorn()));
    }

    // Felhasznalo Lekerdezese ID Alapjan
    @GetMapping("/user/{id}")
    public User findById(@PathVariable Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    /*
    // A Public Chat Része
    @MessageMapping("/application")
    @SendTo("/all/messages")
    public Chat sendMessage(@Payload Chat chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/application.addUser")
    @SendTo("/chat/public")
    public Chat addUser(@Payload Chat chatMessage, SimpMessageHeaderAccessor headerAccessor, Chat chat) {
        // felhasznalo hozzaadasa session-höz (????)
        headerAccessor.getSessionAttributes().put("username", chat.getSender());
        return chatMessage;
    }*/

    // Handling Private Messages
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Chat chat) {
        simpMessagingTemplate.convertAndSendToUser(chat.getReceiver(), "/specific", chat);
        // uzenetek eltarolasa db ben
        messagesRepository.save(new Chat(chat.getId(), chat.getContent(), chat.getSender(), chat.getReceiver()));

    }

    // Send The Contact Details To The Client
    @ModelAttribute("contacts")
    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
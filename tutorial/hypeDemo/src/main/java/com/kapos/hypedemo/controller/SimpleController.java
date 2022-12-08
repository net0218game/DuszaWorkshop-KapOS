package com.kapos.hypedemo.controller;

import com.kapos.hypedemo.model.Chat;
import com.kapos.hypedemo.model.User;
import com.kapos.hypedemo.model.Warning;
import com.kapos.hypedemo.model.repo.MessagesRepository;
import com.kapos.hypedemo.model.repo.UserRepository;
import com.kapos.hypedemo.model.repo.WarningsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RestController
public class SimpleController {

    private final UserRepository userRepository;
    private final MessagesRepository messagesRepository;
    private final WarningsRepository warningsRepository;


    Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public SimpleController(UserRepository userRepository, MessagesRepository messagesRepository, WarningsRepository warningsRepository) {
        this.userRepository = userRepository;
        this.messagesRepository = messagesRepository;
        this.warningsRepository = warningsRepository;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
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


    @GetMapping("/admin")
    public ModelAndView admin(@ModelAttribute Warning warning, Model model) {
        model.addAttribute("warning", warning);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin.html");
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login.html");
        return modelAndView;
    }

    @GetMapping("/me")
    public ModelAndView profile() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile.html");
        return modelAndView;
    }

    @GetMapping("/messages")
    public ModelAndView messages() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("messages.html");
        return modelAndView;
    }

    // ========== Eleresi utak vege ==========

    // Regisztralas
    @PostMapping("/register")
    public ModelAndView insertUser(User user) {
        userRepository.save(new User(user.getUserName(), user.getFirstName(), user.getSecondName(), user.getEmail(), bCryptPasswordEncoder().encode(user.getPassword()), user.getGender(), user.getBorn()));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }

    @PostMapping("/warning")
    public ModelAndView insertWarning(Warning warning) {
        warningsRepository.save(new Warning(warning.getId(), warning.getWarningType(), warning.getTitle(), warning.getContent(), warning.getSignature()));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }

    @PostMapping("/delete")
    public ModelAndView deleteWarning() {

        warningsRepository.deleteAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index.html");
        return modelAndView;
    }

    @GetMapping("/listMessages/{receiver}/{sender}")
    public List<Chat> getAllMessages(@PathVariable String receiver, @PathVariable String sender) {
        return messagesRepository.findChatMessages(sender, receiver);
    }

    @GetMapping("/listGroupMessages/{receiver}")
    public List<Chat> getGroupMessages(@PathVariable String receiver) {
        return messagesRepository.findGroupMessages(receiver);
    }

    @GetMapping("/contacts")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/lastMessage/{receiver}/{sender}")
    public List<Chat> getLastMessages(@PathVariable String receiver, @PathVariable String sender) {
        return messagesRepository.findLastChatMessages(sender, receiver);
    }
    // Felhasznalo Lekerdezese ID Alapjan
    @GetMapping("/user/{id}")
    public User findById(@PathVariable Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // A Public Chat Része
    @MessageMapping("/chat.sendMessage")
    @SendTo("/all/dusza-group")
    public Chat sendMessage(@Payload Chat chatMessage, Chat chat) {
        messagesRepository.save(new Chat(chat.getId(), chat.getContent(), chat.getSender(), chat.getReceiver(), LocalDateTime.now()));
        return chatMessage;
    }

    // Handling Private Messages
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Chat chat) {
        // Uzenet elkuldese felhasznalonak
        simpMessagingTemplate.convertAndSendToUser(chat.getReceiver(), "/specific", chat);
        // Uzenetek eltarolasa db ben
        /* A kesobbiekben a sender-t es a receiver-t meg kell valtoztatnunk senderId és receiverId-re.
        Ezeket majd a Spring Security-vel bejelentkezett felhasznalonak az Id-jevel oldjuk meg.*/
        messagesRepository.save(new Chat(chat.getId(), chat.getContent(), chat.getSender(), chat.getReceiver(), LocalDateTime.now()));
    }

    // ========== Thymeleaf Részek ==========

    // Contactok Kilistazasa
    @ModelAttribute("contacts")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Warningok Kilistazasa
    @ModelAttribute("warnings")
    public List<Warning> getWarnings() {
        return warningsRepository.findAll();
    }

    // ========== Thymeleaf Részek vége ==========
}
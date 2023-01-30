package com.kapos.hypedemo.controller;
import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.search.SearchFeed;
import at.mukprojects.giphy4j.exception.GiphyException;
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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import java.util.Objects;

@Configuration
@RestController
public class SimpleController {

    private final UserRepository userRepository;
    private final MessagesRepository messagesRepository;
    private final WarningsRepository warningsRepository;

    Logger logger = LoggerFactory.getLogger(SimpleController.class);

    // Giphy API Key: t8gAiitRXmPGxIJQA3ESiqjWm9p98t1Q
    Giphy giphy = new Giphy("t8gAiitRXmPGxIJQA3ESiqjWm9p98t1Q");

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

    // Kesobbiekben felhasznalok helyett felhasznalok ID-ja
    @PostMapping("/deleteMessages/{receiver}/{sender}")
    public void deleteMessages (@PathVariable String receiver, @PathVariable String sender){
        messagesRepository.deleteAllByReceiverOrSender(receiver, sender);
    }

    // Content helyett kesobbiekben ID
    @PostMapping("/deleteMessage/{messageId}")
    public void deleteMessage (@PathVariable String messageId){
        messagesRepository.deleteMessage(messageId);
    }

    @GetMapping("/contacts")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/friends/{username}")
    public List<User> getFriends(@PathVariable String username) {
        return messagesRepository.findFriends(username);
    }

    @GetMapping("/lastMessage/{receiver}/{sender}")
    public List<Chat> getLastMessages(@PathVariable String receiver, @PathVariable String sender) {
        return messagesRepository.findLastChatMessages(sender, receiver);
    }

    @PostMapping("/userStatus/{status}")
    public void updateStatus(@PathVariable String status, User user) {
        user.setOnline(Objects.equals(status, "online"));
        logger.info(user.getOnline().toString());
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
        messagesRepository.save(new Chat(chat.getId(), chat.getContent(), chat.getSender(), chat.getReceiver(), chat.getDate()));
        return chatMessage;
    }

    // Handling Private Messages
    @MessageMapping("/private")
    public void sendToSpecificUser(@Payload Chat chat) throws GiphyException {

        if(Objects.equals(chat.getReceiver(), "hypeBot")) {

            String botMessage = "That's A Default Bot Answer";

            if(Objects.equals(chat.getContent(), ".help")) {
                botMessage = """
                        Hey! My name is HYPE! I'm a chat bot. You can use these commands:
                        .help\tDisplays this mesage
                        .say [text]\tI'll say the message you specify in the [text] tag
                        .gif [gif topic]\tRequest a gif about the specified topic after the .gif tag.""";
            } else if (chat.getContent().contains(".say ")) {
                botMessage = chat.getContent().replace(".say ", "");
            } else if (chat.getContent().contains(".gif ")) {
                SearchFeed feed = giphy.search(chat.getContent().replace(".gif ", ""), 1, 0);
                botMessage = feed.getDataList().get(0).getEmbedUrl();
            }

            // Üzenet visszaküldése Botként
            chat.setContent(botMessage);
            chat.setReceiver(chat.getSender());
            chat.setSender("hypeBot");

            simpMessagingTemplate.convertAndSendToUser(chat.getReceiver(), "/specific", chat);
        } else {

            if (chat.getContent().contains(".gif ")) {
                SearchFeed feed = giphy.search(chat.getContent().replace(".gif ", ""), 1, 0);
                chat.setContent(feed.getDataList().get(0).getEmbedUrl());
            }

            // Uzenet elkuldese felhasznalonak
            simpMessagingTemplate.convertAndSendToUser(chat.getReceiver(), "/specific", chat);
            // Uzenetek eltarolasa db ben

            /* A kesobbiekben a sender-t es a receiver-t meg kell valtoztatnunk senderId és receiverId-re.
            Ezeket majd a Spring Security-vel bejelentkezett felhasznalonak az Id-jevel oldjuk meg.*/

            messagesRepository.save(new Chat(chat.getId(), chat.getContent(), chat.getSender(), chat.getReceiver(), chat.getDate()));
        }
    }

    // ========== Thymeleaf Részek ==========

    // Warningok Kilistazasa
    @ModelAttribute("warnings")
    public List<Warning> getWarnings() {
        return warningsRepository.findAll();
    }

    // ========== Thymeleaf Részek vége ==========
}
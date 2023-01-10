package com.kapos.hypedemo.controller;

import com.kapos.hypedemo.model.Chat;
import com.kapos.hypedemo.model.User;
import com.kapos.hypedemo.model.repo.MessagesRepository;
import com.kapos.hypedemo.model.repo.UserRepository;
import com.kapos.hypedemo.model.repo.WarningsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class SimpleControllerTest {

    private MessagesRepository messagesRepository;
    private UserRepository userRepository;
    private WarningsRepository warningsRepository;
    private SimpleController simpleController;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        messagesRepository = Mockito.mock(MessagesRepository.class);
        warningsRepository = Mockito.mock(WarningsRepository.class);
        simpleController = new SimpleController(userRepository, messagesRepository, warningsRepository);
    }

    @Test
    @DisplayName("sendMessage működik")
    void sendMessageCallsMessageRepoSave() {

        Chat chatMessage = new Chat();
        Chat chat = new Chat();
        chat.setSender("Szilard");
        simpleController.sendMessage(chatMessage, chat);
        ArgumentCaptor<Chat> chatArgumentCaptor = ArgumentCaptor.forClass(Chat.class);
        Mockito.verify(messagesRepository, Mockito.times(1)).save(chatArgumentCaptor.capture());

        assertEquals("Szilard", chatArgumentCaptor.getValue().getSender());
    }

    @Test
    @DisplayName("rawPassword nem lehet null")
    void throwsExceptionWhenRawPasswordIsNullAndInsertUserCalled() {
        User user = new User();
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> simpleController.insertUser(user));
        assertEquals("rawPassword cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("inserUser működik")
    void userSaveCalledWhenInsertUser() {
        User user = new User();
        user.setPassword("strongPassword");
        simpleController.insertUser(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(any());
    }
}
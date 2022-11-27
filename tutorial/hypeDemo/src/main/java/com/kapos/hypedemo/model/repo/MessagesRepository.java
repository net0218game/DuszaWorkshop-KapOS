package com.kapos.hypedemo.model.repo;

import com.kapos.hypedemo.model.Chat;
import com.kapos.hypedemo.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;

import java.util.List;

public interface MessagesRepository extends CrudRepository<Chat, Integer> {
    List<Message> findTopBySenderOrReceiver(String sender, String receiver);
}

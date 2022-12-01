package com.kapos.hypedemo.model.repo;

import com.kapos.hypedemo.model.Chat;
import com.kapos.hypedemo.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.Message;

import java.util.List;

public interface MessagesRepository extends CrudRepository<Chat, Integer> {
    List<Chat> findTopBySenderOrReceiver(String sender, String receiver);

    @Query(value = "SELECT * FROM chat WHERE chat.receiver = :user or chat.sender = :user",
            nativeQuery = true)
    List<Chat> findChatMessages(@Param("user") String user);
}

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

    @Query(value = "(SELECT receiver FROM chat WHERE sender = :username) UNION (SELECT sender FROM chat WHERE receiver = :username)",
            nativeQuery = true)
    List findFriends(@Param("username") String username);


    @Query(value = "SELECT * FROM chat WHERE (chat.receiver = :receiver and chat.sender = :sender) or " +
            "(chat.receiver = :sender and chat.sender = :receiver)",
            nativeQuery = true)
    List<Chat> findChatMessages(@Param("sender") String sender, @Param("receiver") String receiver);


    @Query(value = "SELECT * FROM chat WHERE (chat.receiver = :receiver)",
            nativeQuery = true)
    List<Chat> findGroupMessages(@Param("receiver") String receiver);


    @Query(value = "SELECT * FROM chat WHERE (chat.receiver = :receiver and chat.sender = :sender) or " +
            "(chat.receiver = :sender and chat.sender = :receiver) ORDER BY chat.id DESC LIMIT 1",
            nativeQuery = true)
    List<Chat> findLastChatMessages(@Param("sender") String sender, @Param("receiver") String receiver);

    @Query(value = "DELETE FROM chat WHERE (chat.receiver = :receiver and chat.sender = :sender) or  " +
                       "(chat.receiver = :sender and chat.sender = :receiver)",
            nativeQuery = true)
    Chat deleteAllByReceiverOrSender(@Param("sender") String sender, @Param("receiver") String receiver);

    @Query(value = "DELETE FROM chat WHERE (chat.id = :id)",
            nativeQuery = true)
    Chat deleteMessage(@Param("id") String id);

    //DELETE FROM chat WHERE ( chat.receiver = :receiver)

}

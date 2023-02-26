package com.kapos.hypedemo.model.repo;

import com.kapos.hypedemo.model.Unread;
import org.springframework.boot.env.ConfigTreePropertySource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnreadRepository extends CrudRepository<Unread, Integer> {
    @Query(value = "DELETE FROM unread WHERE (unread.receiver = :receiver and unread.sender = :sender)", nativeQuery = true)
    Unread deleteUnreads(@Param("receiver") String receiver, @Param("sender") String sender);

    @Query(value = "SELECT COUNT(*) as 'id', sender, receiver, content, date FROM unread WHERE unread.receiver = :receiver GROUP BY unread.sender", nativeQuery = true)
    List<Unread> findUnreadMessages(@Param("receiver") String receiver);
}

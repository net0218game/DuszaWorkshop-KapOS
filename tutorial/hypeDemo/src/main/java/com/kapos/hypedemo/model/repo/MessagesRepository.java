package com.kapos.hypedemo.model.repo;

import com.kapos.hypedemo.model.Chat;
import org.springframework.data.repository.CrudRepository;

public interface MessagesRepository extends CrudRepository<Chat, Integer> {

}

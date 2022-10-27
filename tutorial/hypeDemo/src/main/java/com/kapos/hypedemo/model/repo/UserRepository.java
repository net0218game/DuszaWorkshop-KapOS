package com.kapos.hypedemo.model.repo;

import com.kapos.hypedemo.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}

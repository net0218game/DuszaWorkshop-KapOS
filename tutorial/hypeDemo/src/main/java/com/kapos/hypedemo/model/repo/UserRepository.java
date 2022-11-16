package com.kapos.hypedemo.model.repo;
import com.kapos.hypedemo.model.User;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByuserName(String username);
    public List<User> findAll();
}


package com.kapos.hypedemo.model.repo;
import com.kapos.hypedemo.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByuserName(String username);
    List<User> findAll();
}


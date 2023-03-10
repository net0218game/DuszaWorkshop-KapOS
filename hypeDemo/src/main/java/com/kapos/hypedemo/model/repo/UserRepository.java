package com.kapos.hypedemo.model.repo;
import com.kapos.hypedemo.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserByuserName(String username);

    @Query(value = "SELECT user_name FROM user",
            nativeQuery = true)
    List findAllUsers();

    List<User> findAll();
}


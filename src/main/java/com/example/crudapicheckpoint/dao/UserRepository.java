package com.example.crudapicheckpoint.dao;

import com.example.crudapicheckpoint.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query(value = "select id, email from users", nativeQuery = true)
    public Iterable<UserView> getUsersNoPassword();

    @Query(value = "select id, email from users where id = ?1", nativeQuery = true)
    public Optional<UserView> getUserByIdNoPassword(long id);

//    @Query(value = "select * from users where email = ?1", nativeQuery = true)
//    public User getUserByEmail(String email);

    public User getDistinctByEmail(String email);
}

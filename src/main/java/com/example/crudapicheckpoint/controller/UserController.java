package com.example.crudapicheckpoint.controller;

import com.example.crudapicheckpoint.dao.UserRepository;
import com.example.crudapicheckpoint.dao.UserView;
import com.example.crudapicheckpoint.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {

        this.repository = repository;
    }

    @GetMapping("/users")
    public Iterable<UserView> getAllUsers() {

        return repository.getUsersNoPassword();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserView> getUserById(@PathVariable long id) {

        UserView user = repository.getUserByIdNoPassword(id).get();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {

        return repository.save(user);
    }

    @PatchMapping("/users/{id}")
    public UserView patchUserById(@PathVariable long id, @RequestBody Map<String, String> map) {

        User getUser = repository.findById(id).get();
        map.forEach((field, value) -> {
            if (field.equals("email")) {
                getUser.setEmail(value);
            }
            if (field.equals("password")) {
                getUser.setPassword(value);
            }
        });
        repository.save(getUser);
        return repository.getUserByIdNoPassword(id).get();

    }

    @DeleteMapping("/users/{id}")
    public Map<String, Long> deleteUserReturnCount(@PathVariable long id) {
        repository.deleteById(id);
        return Map.of("count", repository.count());
    }

//    @PostMapping("/users/authenticate1")
//    public Object postAuthenticateUser(@RequestBody Map<String, String> userNamePass) {
//        User user = repository.getDistinctByEmail(userNamePass.get("email"));
//        boolean authenticated = user.getPassword().equals(userNamePass.get("password"));
//        if (!authenticated) {
//            return Map.of("authenticated", false);
//        }
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> map = new HashMap<>();
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put("id", user.getId());
//        userMap.put("email", user.getEmail());
//
//        map.put("authenticated", true);
//        map.put("user", userMap);
//
//        try {
//            return objectMapper.writeValueAsString(map);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @PostMapping("/users/authenticate")
    public Object postAuthenticateUser2(@RequestBody Map<Object, Object> userNamePass) {

        try {
            User user = repository.getDistinctByEmail((String) userNamePass.get("email"));
            boolean authenticated = user.getPassword().equals(userNamePass.get("password"));
            HashMap<Object, Object> returnedObject = new HashMap<>();
            if (!authenticated) {
                return Map.of("authenticated", false);
            }

            returnedObject.put("authenticated", true);

            returnedObject.put("user", user);
            return returnedObject;
        } catch (Exception e) {
            return Map.of("authenticated", false);
        }

    }
}

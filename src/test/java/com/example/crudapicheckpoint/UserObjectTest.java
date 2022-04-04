package com.example.crudapicheckpoint;


import com.example.crudapicheckpoint.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserObjectTest {

    @Test
    public void userObjectCreated() {
        User testUser = new User();

        assertEquals(true, testUser instanceof User);
    }

    @Test
    public void userConstructorsWork() {
        User testUserNoArgs = new User();
        User testUserAllArgs = new User(1, "test@gmail.com", "password");

        assertEquals(null, testUserNoArgs.getEmail());
        assertEquals("password", testUserAllArgs.getPassword());
    }


}

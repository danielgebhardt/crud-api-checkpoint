package com.example.crudapicheckpoint;

import com.example.crudapicheckpoint.dao.UserRepository;
import com.example.crudapicheckpoint.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    UserRepository repository;

    @Autowired
    MockMvc mvc;

    @Test
    public void getsAllUsers() throws Exception {
        this.mvc.perform(get("/users")).andExpect(content().string("[]"));
    }

    @Test
    @Rollback
    @Transactional
    public void insertUsers() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new User("user", "pass"));

        MockHttpServletRequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        this.mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email", is("user")))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());
    }

    @Test
    @Rollback
    @Transactional
    public void getUsersAfterEntry() throws Exception {

        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        this.mvc.perform(get("/users"))
                .andExpect(jsonPath("$[0].email", is("test1")))
                .andExpect(jsonPath("$[0].password").doesNotHaveJsonPath());
    }

    @Test
    @Rollback
    @Transactional
    public void getUsersAfterWithoutPassword() throws Exception {

        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        this.mvc.perform(get("/users"))
                .andExpect(jsonPath("$[0].password").doesNotHaveJsonPath());
    }

    @Test
    @Rollback
    @Transactional
    public void getUserById() throws Exception {

        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        this.mvc.perform(get("/users/1"))
                .andExpect(jsonPath("$.email", is("test1")))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());
    }

    @Test
    @Rollback
    @Transactional
    public void patchUserByID() throws Exception {

        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        MockHttpServletRequestBuilder request = patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"replacement\"}");

        this.mvc.perform(request).andExpect(jsonPath("$.email", is ("replacement")))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());
    }

    @Test
    @Rollback
    @Transactional
    public void deleteUserById() throws Exception {

        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));
        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));
        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));
        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        MockHttpServletRequestBuilder request = delete("/users/1");

        this.mvc.perform(request).andExpect(jsonPath("$.count", is(7)));

    }

    @Test
    @Rollback
    @Transactional
    public void postAuthenticateFalse() throws Exception {
        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        MockHttpServletRequestBuilder request = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "    \"email\": \"test1\",\n" +
                        "    \"password\": \"sd;flksdf\"\n" +
                        "}");

        this.mvc.perform(request).andExpect(jsonPath("$.authenticated", is(false)));

    }

    @Test
    @Rollback
    @Transactional
    public void postAuthenticateEmailNotFoundFalse() throws Exception {
        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        MockHttpServletRequestBuilder request = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "    \"email\": \"te232131\",\n" +
                        "    \"password\": \"sd;flksdf\"\n" +
                        "}");

        this.mvc.perform(request).andExpect(jsonPath("$.authenticated", is(false)));

    }

    @Test
    @Rollback
    @Transactional
    public void postAuthenticateTrueEmailFound() throws Exception {
        repository.save(new User("test1", "pass1"));
        repository.save(new User("test2", "pass2"));

        MockHttpServletRequestBuilder request = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" +
                        "    \"email\": \"test1\",\n" +
                        "    \"password\": \"pass1\"\n" +
                        "}");

        this.mvc.perform(request).andExpect(jsonPath("$.authenticated", is(true)))
                .andExpect(jsonPath("$.user.email", is("test1")));

    }

}

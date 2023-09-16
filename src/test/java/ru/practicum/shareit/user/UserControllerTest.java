package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createUser() throws Exception {

        UserDto user = new UserDto(1, "dssd", "dsd@mail.ru");

        when(userService.createUser(Mockito.any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    return userDto;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void createUserNullName() throws Exception {

        UserDto user = new UserDto(1, " ", "dsd@mail.ru");

        when(userService.createUser(Mockito.any(UserDto.class)))
                .thenThrow(new RuntimeException());


        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createUserBadEmail() throws Exception {

        UserDto user = new UserDto(1, "dss", "dsdmail.ru");

        when(userService.createUser(Mockito.any(UserDto.class)))
                .thenThrow(new RuntimeException());


        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createUserNullEmail() throws Exception {

        UserDto user = new UserDto(1, "dss", null);

        when(userService.createUser(Mockito.any(UserDto.class)))
                .thenThrow(new RuntimeException());


        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getAllUsers() throws Exception {

        UserDto user = new UserDto(1, "dssd", "dsd@mail.ru");

        when(userService.getAllUsers())
                .thenAnswer(invocationOnMock -> List.of(user));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(List.of(user)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));
    }

    @Test
    void getUser() throws Exception {
        UserDto user = new UserDto(1, "dssd", "dsd@mail.ru");

        when(userService.getUser(Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    int id = invocationOnMock.getArgument(0, Integer.class);
                    return user;
                });

        mvc.perform(get("/users/{id}", 1)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void patchUser() throws Exception {
        UserDto user = new UserDto(1, "dssd", "dsd@mail.ru");

        when(userService.patchUser(Mockito.anyInt(), Mockito.any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    int id = invocationOnMock.getArgument(0, Integer.class);
                    return user;
                });

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void patchUserBadEmail() throws Exception {
        UserDto user = new UserDto(1, "dssd", "dsdmail.ru");

        when(userService.patchUser(Mockito.anyInt(), Mockito.any(UserDto.class)))
                .thenThrow(new RuntimeException());


        mvc.perform(post("/users/{id}", 1)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(405));
    }

    @Test
    void deleteUser() throws Exception {
        UserDto user = new UserDto(1, "dssd", "dsd@mail.ru");


        mvc.perform(delete("/users/{id}", 1)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
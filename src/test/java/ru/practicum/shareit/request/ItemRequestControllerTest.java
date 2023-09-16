package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createRequest() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "sdfd", user, LocalDateTime.now(), new ArrayList<>());

        when(requestService.createRequest(Mockito.anyInt(), Mockito.any(ItemRequestDto.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequestDto itemRequestDto = invocationOnMock.getArgument(1, ItemRequestDto.class);
                    return itemRequestDto;
                });

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(user.getId())))
                .andExpect(jsonPath("$.requester.name", is(user.getName())));
    }

    @Test
    void createRequestBadDescription() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "fd", user, LocalDateTime.now(), new ArrayList<>());

        when(requestService.createRequest(Mockito.anyInt(), Mockito.any(ItemRequestDto.class)))
                .thenThrow(new ValidationException("Bad Description"));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createRequestBadCreated() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "fd", user, LocalDateTime.now().plusSeconds(4), new ArrayList<>());

        when(requestService.createRequest(Mockito.anyInt(), Mockito.any(ItemRequestDto.class)))
                .thenThrow(new ValidationException("Bad Created"));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getRequests() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "sdfd", user, LocalDateTime.now(), new ArrayList<>());

        when(requestService.getRequests(Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(request));

        mvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(List.of(request)))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].requester.id", is(user.getId())))
                .andExpect(jsonPath("$[0].requester.name", is(user.getName())));
    }

    @Test
    void getRequestById() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "sdfd", user, LocalDateTime.now(), new ArrayList<>());

        when(requestService.getRequestId(Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> request);

        mvc.perform(get("/requests/{requestId}", 1)
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(user.getId())))
                .andExpect(jsonPath("$.requester.name", is(user.getName())));
    }

    @Test
    void getRequestsAnotherWrong() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "sdfd", user, LocalDateTime.now(), new ArrayList<>());

        when(requestService.getRequestsAnother(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(request));

        mvc.perform(get("/all")
                        .content(mapper.writeValueAsString(List.of(request)))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void getRequestsAnother() throws Exception {
        User user = User.builder().name("sds").email("sd@mail.ru").id(1).build();
        ItemRequestDto request = new ItemRequestDto(1, "sdfd", user, LocalDateTime.now(), new ArrayList<>());

        when(requestService.getRequestsAnother(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(request));

        mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(List.of(request)))
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].requester.id", is(user.getId())))
                .andExpect(jsonPath("$[0].requester.name", is(user.getName())));
    }
}
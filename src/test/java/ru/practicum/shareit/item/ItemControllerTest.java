package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;


    @Test
    void createItem() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.createItem(Mockito.any(ItemDto.class), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = invocationOnMock.getArgument(0, ItemDto.class);
                    return itemDto;
                });

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void createItemNullName() throws Exception {
        ItemDto item = new ItemDto(1, " ", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.createItem(Mockito.any(ItemDto.class), Mockito.anyInt()))
                .thenThrow(new ValidationException("Bad Name"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createItemNullDescription() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", " ", true, null, null, new ArrayList<>(), 1);

        when(itemService.createItem(Mockito.any(ItemDto.class), Mockito.anyInt()))
                .thenThrow(new ValidationException("Bad Description"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createItemNullAvailable() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", "gh", null, null, null, new ArrayList<>(), 1);

        when(itemService.createItem(Mockito.any(ItemDto.class), Mockito.anyInt()))
                .thenThrow(new ValidationException("Bad Available"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void createComment() throws Exception {

        User user = User.builder().name("fgfg").email("sd@mail.ru").id(2).build();
        Item item = Item.builder().id(1).name("Sd").description("sds").owner(user).available(true).build();
        Comment comment = new Comment(1, "Sdsds", user, item);

        CommentDto commentDto1 = CommentMapper.toCommentDto(comment);
        Thread.sleep(2000);

        when(itemService.createComment(1, 1, commentDto1))
                .thenAnswer(invocationOnMock -> {
                    return commentDto1;
                });

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto1))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto1.getAuthorName())));
    }

    @Test
    void patchItem() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.patchItem(Mockito.any(ItemDto.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = invocationOnMock.getArgument(0, ItemDto.class);
                    return itemDto;
                });

        mvc.perform(patch("/items/{id}", 1)
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void patchItemBadName() throws Exception {
        ItemDto item = new ItemDto(1, " ", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.patchItem(Mockito.any(ItemDto.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new ValidationException("Bad Name"));

        mvc.perform(patch("/items/{id}", 1)
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void patchItemBadDescription() throws Exception {
        ItemDto item = new ItemDto(1, "fd", " ", true, null, null, new ArrayList<>(), 1);

        when(itemService.patchItem(Mockito.any(ItemDto.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new ValidationException("Bad Description"));

        mvc.perform(patch("/items/{id}", 1)
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void patchItemBadAvailable() throws Exception {
        ItemDto item = new ItemDto(1, "fd", "fd", null, null, null, new ArrayList<>(), 1);

        when(itemService.patchItem(Mockito.any(ItemDto.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new ValidationException("Bad Available"));

        mvc.perform(patch("/items/{id}", 1)
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void getItem() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.getItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> item);

        mvc.perform(get("/items/{id}", 1)
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void getItems() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.getItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(item));

        mvc.perform(get("/items", 1)
                        .content(mapper.writeValueAsString(List.of(item)))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));
    }


    @Test
    void searchBadText() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(item));

        mvc.perform(get("/search")
                        .content(mapper.writeValueAsString(List.of(item)))
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "aaaa")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void search() throws Exception {
        ItemDto item = new ItemDto(1, "DSSds", "dss", true, null, null, new ArrayList<>(), 1);

        when(itemService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> List.of(item));

        mvc.perform(get("/items/search")
                        .content(mapper.writeValueAsString(List.of(item)))
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "aaaa")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(1), Integer.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));
    }
}
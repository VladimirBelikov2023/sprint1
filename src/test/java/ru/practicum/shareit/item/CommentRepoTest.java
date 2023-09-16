package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepoTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private CommentRepo commentRepo;

    @DirtiesContext
    @Test
    public void findByItem() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").build();
        itemRepository.save(item);

        Comment comment = new Comment(1, "sdsd", user, item);
        commentRepo.save(comment);

        List<Comment> comments = commentRepo.findByItem(item);
        Comment comment1 = comments.get(0);
        assertEquals(comments.size(), 1);
        assertEquals(comment.getId(), 1);
        assertEquals(comment.getUser().getName(), user.getName());
        assertEquals(comment.getItem().getName(), item.getName());
    }

}
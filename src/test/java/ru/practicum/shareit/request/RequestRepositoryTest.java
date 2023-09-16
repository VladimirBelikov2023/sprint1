package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RequestRepository requestRepository;

    @DirtiesContext
    @Test
    public void getAllByAnotherTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        User user2 = User.builder().name("another").email("another@mail.ru").build();
        userRepo.save(user2);
        ItemRequest request = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(request);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(request).build();
        itemRepository.save(item);

        List<ItemRequest> requests = requestRepository.getAllByAnother(user2.getId(), PageRequest.of(0, 1));
        ItemRequest request1 = requests.get(0);
        assertEquals(requests.size(), 1);
        assertEquals(request1.getId(), request.getId());
    }

    @DirtiesContext
    @Test
    public void getByRequestIdTest() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest request = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(request);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(request).build();
        itemRepository.save(item);

        List<ItemRequest> requests = requestRepository.getByRequesterId(user.getId(), Sort.by("created").descending());
        ItemRequest request1 = requests.get(0);
        assertEquals(requests.size(), 1);
        assertEquals(request1.getId(), request.getId());
    }
}
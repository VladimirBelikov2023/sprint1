package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RequestRepository requestRepository;

    @DirtiesContext
    @Test
    public void getByRequestId() {
        User user = User.builder().name("sddd").email("sdsddf@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sdds").description("Sdds").request(itemRequest).build();
        itemRepository.save(item);

        List<Item> items = itemRepository.getByRequestId(itemRequest.getId());
        Item item1 = items.get(0);
        assertEquals(items.size(), 1);
        assertEquals(itemRequest.getId(), item1.getRequest().getId());
        assertEquals(itemRequest.getRequester().getName(), item1.getRequest().getRequester().getName());
        assertEquals(itemRequest.getDescription(), item1.getRequest().getDescription());
    }


    @DirtiesContext
    @Test
    public void search() {
        User user = User.builder().name("sdd").email("sdsd@mail.ru").build();
        userRepo.save(user);
        ItemRequest itemRequest = ItemRequest.builder().requester(user).id(1).description("df").created(LocalDateTime.now()).build();
        requestRepository.save(itemRequest);

        Item item = Item.builder().id(1).owner(user).available(true).name("Sds").description("Sds").request(itemRequest).build();
        itemRepository.save(item);

        List<Item> items = itemRepository.search("ds", PageRequest.of(0, 1));
        Item item1 = items.get(0);
        assertEquals(items.size(), 1);
        assertEquals(item.getId(), item1.getId());
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getOwner().getName(), item1.getOwner().getName());
    }

}
package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query(" select i from ItemRequest i where i.requester.id != ?1")
    List<ItemRequest> getAllByAnother(int id, Pageable pageable);

    List<ItemRequest> getByRequesterId(int id, Sort sort);
}

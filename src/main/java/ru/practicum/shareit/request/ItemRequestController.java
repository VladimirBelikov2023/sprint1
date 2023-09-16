package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@AllArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") int id, @RequestBody @Validated(Create.class) ItemRequestDto requestDto) {
        return requestService.createRequest(id, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") int owner) {
        return requestService.getRequests(owner);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") int owner, @PathVariable int requestId) {
        return requestService.getRequestId(owner, requestId);
    }


    @GetMapping("/all")
    public List<ItemRequestDto> getRequestsAnother(@RequestHeader("X-Sharer-User-Id") int owner, @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from, @RequestParam(required = false, defaultValue = "1000") @Positive int size) {
        return requestService.getRequestsAnother(owner, from, size);
    }

}

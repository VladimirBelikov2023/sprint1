package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    public void commentDto() throws IOException {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1)
                .requester(new User())
                .created(LocalDateTime.now())
                .description("sdsd")
                .build();
        JsonContent<ItemRequestDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).hasJsonPathValue("$.description");
    }

}
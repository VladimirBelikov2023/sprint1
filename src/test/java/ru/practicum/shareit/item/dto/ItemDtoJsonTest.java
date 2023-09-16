package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void commentDto() throws IOException {
        ItemDto dto = ItemDto.builder()
                .id(1)
                .name("sdsd")
                .description("sdsd")
                .available(true)
                .build();
        JsonContent<ItemDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPathValue("$.name");
        assertThat(result).hasJsonPathValue("$.description");
        assertThat(result).hasJsonPathValue("$.available");
    }

}
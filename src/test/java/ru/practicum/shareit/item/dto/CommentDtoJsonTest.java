package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    public void commentDto() throws IOException {
        CommentDto commentDto = new CommentDto(1, "sdsd", "sdd", 1);
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(new Long(commentDto.getItemId()).intValue());
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).hasJsonPathValue("$.authorName");
    }

}
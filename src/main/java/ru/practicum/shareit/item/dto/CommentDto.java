package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {
    private int id;
    @NotNull
    private String text;

    private String authorName;
    private final LocalDateTime created = LocalDateTime.now();
    @NotNull
    private int itemId;
}

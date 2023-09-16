package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "text", nullable = false, length = 4000)
    @NotNull
    private String text;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "author_id", nullable = false)
    private User user;
    @NotNull
    @Column(name = "created", nullable = false)
    private final LocalDateTime created = LocalDateTime.now();
    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}

package ru.practicum.shareit.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    public Item(Integer id, String name, String description, @NonNull Boolean available, User owner, ItemRequest itemRequest) {
        if (Objects.isNull(name) || Objects.isNull(description) || name.isEmpty() || description.isEmpty()) {
            throw new NullObjectException("Значение имени или описания не могут быть пустыми");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.itemRequest = itemRequest;
    }
}


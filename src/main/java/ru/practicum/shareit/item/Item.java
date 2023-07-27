package ru.practicum.shareit.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Objects;

@Data
@NoArgsConstructor
public class Item {
    private Integer id;
    private String name;
    private String description;
    @NonNull
    private Boolean available;
    private Integer owner;
    private ItemRequest itemRequest;

    public Item(Integer id, String name, String description, @NonNull Boolean available, Integer owner, ItemRequest itemRequest) {
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


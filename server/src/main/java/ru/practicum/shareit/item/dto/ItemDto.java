package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Integer requestId;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private List<CommentDto> comments;

    public ItemDto(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}

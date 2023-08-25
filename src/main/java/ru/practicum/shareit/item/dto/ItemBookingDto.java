package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemBookingDto {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}

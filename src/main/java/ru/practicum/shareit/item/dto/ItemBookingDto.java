package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemBookingDto {
    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}

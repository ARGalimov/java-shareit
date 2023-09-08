package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(PostBookingDto postBookingDto, Integer userId);

    BookingDto findById(Integer userId, Integer bookingId);

    List<BookingDto> findUserBooking(Integer userId, String stateParam, Integer from, Integer size);

    List<BookingDto> findItemBooking(Integer userId, String stateParam, Integer from, Integer size);

    BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved);
}

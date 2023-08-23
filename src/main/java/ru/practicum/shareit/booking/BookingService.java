package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Integer userId, PostBookingDto postBookingDto);

    BookingDto findById(Integer userId, Integer bookingId);

    List<BookingDto> findUserBooking(Integer userId, String stateParam);

    List<BookingDto> findItemBooking(Integer userId, String stateParam);

    BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved);
}

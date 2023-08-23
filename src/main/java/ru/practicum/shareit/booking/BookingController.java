package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @Valid @RequestBody PostBookingDto postBookingDto) {
        return bookingService.create(userId, postBookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findUserBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam) {
        return bookingService.findUserBooking(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingDto> findItemBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(defaultValue = "ALL", name = "state") String stateParam) {
        return bookingService.findItemBooking(userId, stateParam);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapperImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemStorage itemStorage;

    @Transactional
    @Override
    public BookingDto create(Integer userId, PostBookingDto postBookingDto) {
        User booker = new UserMapperImpl().toNewEntity(userService.findUser(userId));
        log.info("Определен Booker");
        Integer bookerId = booker.getId();
        Integer itemId = postBookingDto.getItemId();
        log.info("Определена вещь");
        Item item = itemStorage.findById(itemId).orElseThrow(() -> throwNotFoundItemException("Предмет с id " +
                itemId + " не найден!"));
        checkItemAvailable(item);
        Booking booking = BookingMapper.mapToBooking(booker, item, postBookingDto, BookingStatus.WAITING);
        checkBookingDate(booking);

        if (bookerId.equals(item.getOwner().getId())) {
            String message = "Предмет " + itemId + " не доступен для бронирования владельцем " + bookerId;
            log.warn(message);
            throw new PermissionException(message);
        } else {
            log.info("Предмет {} бронирует не владелец, норм.", itemId);
        }

        return BookingMapper.mapToBookingDto(bookingStorage.save(booking));
    }

    @Override
    public BookingDto findById(Integer userId, Integer bookingId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> throwNotFoundItemException(
                "Бронирование с id " + bookingId + " не найдено!"));

        Integer bookerId = booking.getBooker().getId();
        Integer ownerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            String message = "У пользователя " + userId + " нет прав на просмотр бронирования " + bookingId;
            log.warn(message);
            throw new PermissionException(message);
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> findUserBooking(Integer userId, String stateParam) {
        BookingState state = stateToEnum(stateParam);
        userService.findUser(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(userId);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingStorage.findByBookerIdCurrDate(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public List<BookingDto> findItemBooking(Integer userId, String stateParam) {
        BookingState state = stateToEnum(stateParam);
        userService.findUser(userId);
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllItemBooking(userId);
                break;
            case PAST:
                bookings = bookingStorage.findAllItemBookingEndIsBefore(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingStorage.findAllItemBookingAndStartIsAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingStorage.findAllItemBookingCurrDate(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingStorage.findAllItemBookingStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllItemBookingStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }

        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved) {
        userService.findUser(userId);
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> throwNotFoundItemException(
                "Бронирование с id " + bookingId + " не найдено!"));
        itemService.checkPermissions(userId, booking.getItem());
        checkBookingStatus(booking);
        BookingStatus status = (approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking.setStatus(status);

        return BookingMapper.mapToBookingDto(bookingStorage.save(booking));
    }

    private ObjectNotFoundException throwNotFoundItemException(String message) {
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }

    private void checkItemAvailable(Item item) {
        if (!item.isAvailable()) {
            String message = "Предмет " + item.getId() + " не доступен для бронирования";
            log.warn(message);
            throw new ItemNotAvailableException(message);
        }
        log.info("Предмет {} доступен для бронирования", item.getId());
    }

    private void checkBookingDate(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getStart().isAfter(booking.getEnd()) ||
                booking.getEnd().isEqual(booking.getStart())) {
            String message = "Некорректная дата бронирования";
            log.warn(message);
            throw new IncorrectDateException(message);
        }
        log.info("Дата бронирования корректная ");
    }

    private void checkBookingStatus(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            String message = "Бронирование уже одобрено";
            log.warn(message);
            throw new BookingAlreadyApproveException(message);
        }
    }

    private BookingState stateToEnum(String stateParam) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            String message = "Unknown state: UNSUPPORTED_STATUS";
            log.warn(message);
            throw new StateIsNotSupportException(message);
        }
        return state;
    }
}

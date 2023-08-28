package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.OffsetPage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final UserStorage userStorage;
    private final ItemService itemService;
    private final ItemStorage itemStorage;

    @Transactional
    @Override
    public BookingDto create(PostBookingDto postBookingDto, Integer userId) {
        User booker = userStorage.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new ObjectNotFoundException("Пользователь не найден");
        });
        log.info("Определен Booker");
        Integer bookerId = booker.getId();
        Integer itemId = postBookingDto.getItemId();
        log.info("Определена вещь");
        Item item = itemStorage.findById(itemId).orElseThrow(() -> throwNotFoundItemException("Предмет с id " +
                itemId + " не найден!"));
        checkItemAvailable(item);
        checkBookingDate(postBookingDto);
        Booking booking = BookingMapper.mapToBooking(booker, item, postBookingDto, BookingStatus.WAITING);

        if (bookerId.equals(item.getOwner().getId())) {
            String message = "Предмет " + itemId + " не доступен для бронирования владельцем " + bookerId;
            log.warn(message);
            throw new PermissionException(message);
        } else {
            log.info("Предмет {} бронирует не владелец, норм.", itemId);
        }
        Booking savedBooking = bookingStorage.save(booking);
        return BookingMapper.mapToBookingDto(savedBooking);
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
    public List<BookingDto> findUserBooking(Integer userId, String stateParam, Integer from, Integer size) {
        BookingState state = stateToEnum(stateParam);
        userService.findUser(userId);
        List<Booking> bookings;
        Pageable pageable = new OffsetPage(from, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(userId, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findByBookerIdCurrDate(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = new ArrayList<>();
        }

        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public List<BookingDto> findItemBooking(Integer userId, String stateParam, Integer from, Integer size) {
        BookingState state = stateToEnum(stateParam);
        userService.findUser(userId);
        List<Booking> bookings;
        Pageable pageable = new OffsetPage(from, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllItemBooking(userId, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findAllItemBookingEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findAllItemBookingAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findAllItemBookingCurrDate(userId, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findAllItemBookingStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findAllItemBookingStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = new ArrayList<>();
        }

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

    private void checkBookingDate(PostBookingDto bookingDto) {
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
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
